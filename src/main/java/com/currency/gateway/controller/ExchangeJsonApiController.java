package com.currency.gateway.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.currency.gateway.collector.RatesCollector;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeRequest;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeResponse;
import com.currency.gateway.model.latestexchange.LatestExchangeRequest;
import com.currency.gateway.model.latestexchange.LatestExchangeResponse;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.service.ApiRequestService;
import com.currency.gateway.service.ExchangeApiService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller to handle all requests to the JSON API.
 */
@Slf4j
@RestController
@RequestMapping("/json_api")
public class ExchangeJsonApiController {

    @Autowired
    ExchangeApiService exchangeApiService;

    @Autowired
    ApiRequestService apiRequestService;

    @Autowired
    RatesCollector ratesCollector;

    @Autowired
    CurrencyRepository currencyRepository;

    @PostMapping(value = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processGetLatestExchange(@RequestBody LatestExchangeRequest request) {
        log.info("Get latest exchange data for currency {}", request.getCurrency());
        if (apiRequestService.isDuplicate(request.getRequestId())) {
            log.error("Duplicated api request for latest exchange. Request ID: {}", request.getRequestId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
        }

        LatestExchangeResponse response = exchangeApiService.processLatestExchangeRequest(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/history", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processGetHistoricalExchange(
            @RequestBody HistoricalExchangeRequest request) {
        log.info("Get historical exchange data for currency {}", request.getCurrency());
        if (apiRequestService.isDuplicate(request.getRequestId())) {
            log.error("Duplicated api request for exchange history. Request ID: {}", request.getRequestId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
        }

        HistoricalExchangeResponse response = exchangeApiService.processHistoryRequest(request);
        return ResponseEntity.ok(response);
    }

    // For testing
    @PostMapping(value = "/dbsync", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> collectRates() {
        ratesCollector.collectRates();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/currencies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Currency>> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return ResponseEntity.ok(currencies);
    }
}