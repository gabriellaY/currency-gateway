package com.currency.gateway.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.currency.gateway.collector.StatisticsCollector;
import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.model.ExchangeApiRequest;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.ServiceRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class to handle all operations on the ApiRequest entities.
 */
@Service
@Slf4j
public class ApiRequestService {

    private final Set<String> processedRequestIds = new HashSet<>();
    private final CurrencyRepository currencyRepository;
    private final ServiceRepository serviceRepository;
    private final StatisticsCollector statisticsCollector;

    @Autowired
    public ApiRequestService(CurrencyRepository currencyRepository,
                             StatisticsCollector statisticsCollector,
                             ServiceRepository serviceRepository) {
        this.currencyRepository = currencyRepository;
        this.statisticsCollector = statisticsCollector;
        this.serviceRepository = serviceRepository;
    }

    public synchronized boolean isDuplicate(String requestId) {
        return !processedRequestIds.add(requestId);
    }

    @Transactional
    public ApiRequest processApiRequest(ExchangeApiRequest request) {
        Optional<Currency> currencyOptional = currencyRepository.findBySymbol(request.getCurrency());

        if (currencyOptional.isEmpty()) {
            log.error("Currency {} is not present in the DB.", request.getCurrency());
            throw new RuntimeException("No such currency present in the DB.");
        }

        com.currency.gateway.entity.Service service = serviceRepository.findByName(request.getService())
                .orElseThrow(() -> new RuntimeException("No such service registered with the API."));
        
        ApiRequest apiRequest =
                new ApiRequest(request.getRequestId(), service, request.getClient(), request.getTimestamp());
        ApiRequest saved = statisticsCollector.saveApiRequestStatistics(apiRequest);

        return saved;
    }
}
