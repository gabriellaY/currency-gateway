package com.currency.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.currency.gateway.model.HistoricalExchangeRequest;
import com.currency.gateway.model.HistoricalExchangeResponse;
import com.currency.gateway.model.LatestExchangeRequest;
import com.currency.gateway.model.LatestExchangeResponse;
import com.currency.gateway.service.ApiRequestService;
import com.currency.gateway.service.CurrencyJsonApiService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller to handle all requests to the JSON API.
 */
@Slf4j
@RestController
@RequestMapping("/json_api")
public class ExchangeJsonApiController {

    @Autowired
    CurrencyJsonApiService currencyJsonApiService;

    @Autowired
    ApiRequestService apiRequestService;

    @PostMapping(value = "/current", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LatestExchangeResponse> processGetLatestExchange(@RequestBody LatestExchangeRequest request) {
        log.info("Get latest exchange data for currency {}", request.getCurrency());
        if (apiRequestService.isDuplicate(request.getRequestId())) {
            log.error("Duplicated api request.");
            return ResponseEntity.status(409).body(new LatestExchangeResponse());
        }

        LatestExchangeResponse response = currencyJsonApiService.processLatestExchangeRequest(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/history", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HistoricalExchangeResponse> processGetHistoricalExchange(
            @RequestBody HistoricalExchangeRequest request) {
        log.info("Get historical exchange data for currency {}", request.getCurrency());
        if (apiRequestService.isDuplicate(request.getRequestId())) {
            log.error("Duplicated api request.");
            return ResponseEntity.status(409).body(new HistoricalExchangeResponse());
        }

        HistoricalExchangeResponse response = currencyJsonApiService.processHistoryRequest(request);
        return ResponseEntity.ok(response);
    }
}