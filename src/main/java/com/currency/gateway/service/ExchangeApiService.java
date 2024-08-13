package com.currency.gateway.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.currency.gateway.dto.LatestExchangeDto;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.exception.CurrencyNotFoundException;
import com.currency.gateway.exception.ExchangeDataNotFoundException;
import com.currency.gateway.mapper.LatestExchangeMapper;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeRequest;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeResponse;
import com.currency.gateway.model.latestexchange.LatestExchangeRequest;
import com.currency.gateway.model.latestexchange.LatestExchangeResponse;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.HistoricalExchangeRepository;
import com.currency.gateway.repository.LatestExchangeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for json API requests.
 */
@Service
@Slf4j
public class ExchangeApiService {

    private final LatestExchangeRepository latestExchangeRepository;
    private final HistoricalExchangeRepository historicalExchangeRepository;
    private final CurrencyRepository currencyRepository;
    private final ApiRequestService apiRequestService;
    private final CacheService cacheService;
    private final LatestExchangeMapper latestExchangeMapper;

    @Autowired
    public ExchangeApiService(LatestExchangeRepository latestExchangeRepository,
                              HistoricalExchangeRepository historicalExchangeRepository,
                              ApiRequestService apiRequestService, LatestExchangeMapper latestExchangeMapper,
                              CurrencyRepository currencyRepository, CacheService cacheService) {
        this.latestExchangeRepository = latestExchangeRepository;
        this.historicalExchangeRepository = historicalExchangeRepository;
        this.apiRequestService = apiRequestService;
        this.latestExchangeMapper = latestExchangeMapper;
        this.currencyRepository = currencyRepository;
        this.cacheService = cacheService;
    }

    @Transactional
    public LatestExchangeResponse processLatestExchangeRequest(LatestExchangeRequest request) {
        apiRequestService.processApiRequest(request);

        Currency currency = currencyRepository.findBySymbol(request.getCurrency())
                .orElseThrow(() -> new CurrencyNotFoundException("No such currency present in the DB."));

        ArrayList<LinkedHashMap<String, Object>> cachedExchangeResponse =
                (ArrayList<LinkedHashMap<String, Object>>) cacheService.getLatestExchanges(request.getCurrency());

        List<LatestExchangeDto> dtoList;

        if (cachedExchangeResponse != null) {
            log.info("Getting latest exchange from the cache.");
            dtoList = cachedExchangeResponse.stream()
                    .map(cached -> latestExchangeMapper.mapCachedResponseToLatestExchange(cached))
                    .collect(Collectors.toList());
            
        } else {
            log.info("Getting latest exchange from the DB.");
            List<LatestExchange> latestExchange = latestExchangeRepository.findByBaseCurrency(currency).orElseThrow(
                    () -> new ExchangeDataNotFoundException(
                            "No exchange data found for currency: " + request.getCurrency()));
            dtoList = latestExchange.stream().map(latestExchangeMapper::toDto).collect(Collectors.toList());
        }

        log.info("Found latest exchange for currency {}", request.getCurrency());
        
        return new LatestExchangeResponse(dtoList);
    }

    @Transactional
    public HistoricalExchangeResponse processHistoryRequest(HistoricalExchangeRequest request) {
        apiRequestService.processApiRequest(request);

        // Calculate the start time based on the period in hours
        long currentTimeMillis = System.currentTimeMillis();
        long periodInMillis = request.getPeriod() * 3600000L;
        long startTime = currentTimeMillis - periodInMillis;

        List<HistoricalExchange> historicalExchangeData =
                historicalExchangeRepository.findByBaseCurrencyAndTimestamp(request.getCurrency(), startTime)
                        .orElseThrow(() -> new ExchangeDataNotFoundException(
                                "No historical exchange data found for currency: " + request.getCurrency()));

        log.info("Found historical exchange for currency {}", request.getCurrency());

        List<HistoricalExchangeResponse.HistoricalExchangeData> exchangeHistory = new ArrayList<>();
        for (HistoricalExchange exchange : historicalExchangeData) {
            HistoricalExchangeResponse.HistoricalExchangeData data =
                    new HistoricalExchangeResponse.HistoricalExchangeData(exchange.getTimestamp(), exchange.getRate());

            exchangeHistory.add(data);
        }

        return new HistoricalExchangeResponse(request.getTimestamp(), request.getCurrency(), request.getPeriod(),
                                              exchangeHistory);
    }
}
