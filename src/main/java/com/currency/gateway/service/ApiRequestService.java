package com.currency.gateway.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.model.ExchangeApiRequest;
import com.currency.gateway.repository.ApiRequestRepository;
import com.currency.gateway.repository.CurrencyRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class to handle all operations on the ApiRequest entities.
 */
@Service
@Slf4j
public class ApiRequestService {

    private final Set<UUID> processedRequestIds = new HashSet<>();
    private final CurrencyRepository currencyRepository;
    private final ApiRequestRepository apiRequestRepository;

    @Autowired
    public ApiRequestService(CurrencyRepository currencyRepository, ApiRequestRepository apiRequestRepository) {
        this.currencyRepository = currencyRepository;
        this.apiRequestRepository = apiRequestRepository;
    }

    public synchronized boolean isDuplicate(UUID requestId) {
        return !processedRequestIds.add(requestId);
    }

    public ApiRequest processApiRequest(ExchangeApiRequest request) {
        Optional<Currency> currencyOptional = currencyRepository.findBySymbol(request.getCurrency());

        if (currencyOptional.isEmpty()) {
            log.error("Currency {} is not present in the DB.", request.getCurrency());
            throw new RuntimeException("No such currency present in the DB.");
        }
        Currency currency = currencyOptional.get();
        ApiRequest apiRequest =
                new ApiRequest(request.getRequestId(), request.getClient(), currency, 0, request.getTimestamp());
        log.info("Saving API request. {}", apiRequest);
        ApiRequest saved = apiRequestRepository.save(apiRequest);

        return saved;
    }

}
