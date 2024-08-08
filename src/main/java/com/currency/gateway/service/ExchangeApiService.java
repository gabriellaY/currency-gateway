package com.currency.gateway.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.currency.gateway.dto.LatestExchangeDto;
import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.mapper.LatestExchangeMapper;
import com.currency.gateway.model.HistoricalExchangeRequest;
import com.currency.gateway.model.HistoricalExchangeResponse;
import com.currency.gateway.model.LatestExchangeRequest;
import com.currency.gateway.model.LatestExchangeResponse;
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

    private final ApiRequestService apiRequestService;

    private final LatestExchangeMapper latestExchangeMapper;

    @Autowired
    public ExchangeApiService(LatestExchangeRepository latestExchangeRepository,
                              HistoricalExchangeRepository historicalExchangeRepository,
                              ApiRequestService apiRequestService, LatestExchangeMapper latestExchangeMapper) {
        this.latestExchangeRepository = latestExchangeRepository;
        this.historicalExchangeRepository = historicalExchangeRepository;
        this.apiRequestService = apiRequestService;
        this.latestExchangeMapper = latestExchangeMapper;
    }

    @Transactional
    public LatestExchangeResponse processLatestExchangeRequest(LatestExchangeRequest request) {
        ApiRequest savedRequest = apiRequestService.processApiRequest(request);

        List<LatestExchange> latestExchange = latestExchangeRepository.findByBaseCurrency(savedRequest.getCurrency())
                .orElseThrow(
                        () -> new RuntimeException("No exchange data found for currency: " + request.getCurrency()));

        List<LatestExchangeDto> dtoList = latestExchange.stream()
                .map(latestExchangeMapper::toDto)
                .toList();

        log.info("Found latest exchange for currency {}", savedRequest.getCurrency().getSymbol());
        return new LatestExchangeResponse(dtoList);
    }

    @Transactional
    public HistoricalExchangeResponse processHistoryRequest(HistoricalExchangeRequest request) {
        ApiRequest savedRequest = apiRequestService.processApiRequest(request);

        // Calculate the start time based on the period in hours
        long currentTimeMillis = System.currentTimeMillis();
        long periodInMillis = request.getPeriod() * 3600000L;
        long startTime = currentTimeMillis - periodInMillis;

        List<HistoricalExchange> historicalExchangeData =
                historicalExchangeRepository.findByBaseCurrencyAndTimestamp(request.getCurrency(), startTime)
                        .orElseThrow(() -> new RuntimeException("No historical exchange data found for currency: "
                                                                + request.getCurrency()));

        log.info("Found historical exchange for currency {}", savedRequest.getCurrency().getSymbol());

        List<HistoricalExchangeResponse.HistoricalExchangeData> exchangeHistory = new ArrayList<>();
        for (HistoricalExchange exchange : historicalExchangeData) {
            HistoricalExchangeResponse.HistoricalExchangeData data =
                    new HistoricalExchangeResponse.HistoricalExchangeData(exchange.getTimestamp(), exchange.getRate());

            exchangeHistory.add(data);
        }

        return new HistoricalExchangeResponse(savedRequest.getTimestamp(), savedRequest.getCurrency().getSymbol(),
                                              savedRequest.getPeriod(), exchangeHistory);
    }

}
