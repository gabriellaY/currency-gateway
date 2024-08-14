package com.currency.gateway.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.currency.gateway.collector.StatisticsCollector;
import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.exception.CurrencyNotFoundException;
import com.currency.gateway.exception.ServiceNotFoundException;
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

    private final CurrencyRepository currencyRepository;
    private final ServiceRepository serviceRepository;
    private final StatisticsCollector statisticsCollector;
    private final RabbitMqPublisher rabbitMqPublisher;

    @Autowired
    public ApiRequestService(CurrencyRepository currencyRepository,
                             StatisticsCollector statisticsCollector,
                             ServiceRepository serviceRepository,
                             RabbitMqPublisher rabbitMqPublisher) {
        this.currencyRepository = currencyRepository;
        this.statisticsCollector = statisticsCollector;
        this.serviceRepository = serviceRepository;
        this.rabbitMqPublisher = rabbitMqPublisher;
    }

    @Transactional
    public ApiRequest processApiRequest(ExchangeApiRequest request) {
        currencyRepository.findBySymbol(request.getCurrency())
                .orElseThrow(() -> new CurrencyNotFoundException("No such currency present in the DB."));
                
        com.currency.gateway.entity.Service service = serviceRepository.findByName(request.getService())
                .orElseThrow(() -> new ServiceNotFoundException("No such service registered with the API."));

        ApiRequest apiRequest =
                new ApiRequest(request.getRequestId(), service, request.getClient(), request.getTimestamp());
        ApiRequest saved = statisticsCollector.saveApiRequestStatistics(apiRequest);

        rabbitMqPublisher.publishApiRequest(apiRequest);

        return saved;
    }
}
