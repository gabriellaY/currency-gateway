package com.currency.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.currency.gateway.model.Command;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeRequest;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeResponse;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeXmlRequest;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeXmlResponse;
import com.currency.gateway.model.latestexchange.LatestExchangeRequest;
import com.currency.gateway.model.latestexchange.LatestExchangeResponse;
import com.currency.gateway.model.latestexchange.LatestExchangeXmlRequest;
import com.currency.gateway.model.latestexchange.LatestExchangeXmlResponse;
import com.currency.gateway.service.CacheService;
import com.currency.gateway.service.ExchangeApiService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller to handle all requests to the XML API.
 */
@Slf4j
@RestController
@RequestMapping("/xml_api")
public class ExchangeXmlApiController {

    private final ExchangeApiService exchangeApiService;
    private final CacheService cacheService;

    @Autowired
    public ExchangeXmlApiController(ExchangeApiService exchangeApiService, CacheService cacheService) {
        this.exchangeApiService = exchangeApiService;
        this.cacheService = cacheService;
    }

    @PostMapping(value = "/command", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> processCommand(@RequestBody Command command) {
        if (command.getLatestExchangeXmlRequest() != null) {

            log.info("Processing latest exchange XML request.");
            LatestExchangeXmlRequest latestExchangeXmlRequest = command.getLatestExchangeXmlRequest();

            LatestExchangeRequest request =
                    new LatestExchangeRequest(command.getId(), latestExchangeXmlRequest.getCurrency(),
                                              latestExchangeXmlRequest.getConsumer(), System.currentTimeMillis(),
                                              command.getService());

            if (cacheService.isDuplicateApiRequest(request)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
            }

            log.info("XML API get latest exchange data for currency {}", request.getCurrency());

            LatestExchangeResponse response = exchangeApiService.processLatestExchangeRequest(request);
            LatestExchangeXmlResponse xmlResponse = new LatestExchangeXmlResponse(response);

            return ResponseEntity.ok(xmlResponse);

        } else if (command.getHistoricalExchangeXmlRequest() != null) {
            log.info("Processing historical exchange XML request.");

            HistoricalExchangeXmlRequest historicalExchangeXmlRequest = command.getHistoricalExchangeXmlRequest();

            HistoricalExchangeRequest request =
                    new HistoricalExchangeRequest(command.getId(), historicalExchangeXmlRequest.getCurrency(),
                                                  historicalExchangeXmlRequest.getConsumer(),
                                                  System.currentTimeMillis(), historicalExchangeXmlRequest.getPeriod(),
                                                  command.getService());

            if (cacheService.isDuplicateApiRequest(request)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
            }

            log.info("XML API get historical exchange data for currency {}", request.getCurrency());

            HistoricalExchangeResponse response = exchangeApiService.processHistoryRequest(request);
            HistoricalExchangeXmlResponse xmlResponse = new HistoricalExchangeXmlResponse(response);

            return ResponseEntity.ok(xmlResponse);
        }

        return ResponseEntity.badRequest().body("Invalid command");
    }
}
