package com.currency.gateway.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.repository.ApiRequestRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to handle the collection of requests statistics.
 */
@Service
@Slf4j
public class StatisticsCollector {

    private final ApiRequestRepository apiRequestRepository;

    @Autowired
    public StatisticsCollector(ApiRequestRepository apiRequestRepository) {
        this.apiRequestRepository = apiRequestRepository;
    }

    @Transactional
    public ApiRequest saveApiRequestStatistics(ApiRequest apiRequest) {
        log.info("Saving API request. {}", apiRequest);
        ApiRequest saved = apiRequestRepository.save(apiRequest);
        log.info("API request saved.");

        return saved;
    }
}
