package com.currency.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.currency.gateway.configuration.FixerConfig;
import com.currency.gateway.model.CurrenciesResponse;
import com.currency.gateway.model.FixerLatestRatesResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Client to make API calls to fixer.io collecting currency data.
 */
@Component
@Slf4j
public class FixerClient {

    @Autowired
    private RestTemplate fixerRestTemplate;

    @Autowired
    private FixerConfig fixerConfig;

    public FixerLatestRatesResponse getLatestRates() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerConfig.getBaseUrl() + fixerConfig.getLatestRatesUrl())
                .queryParam("access_key", fixerConfig.getApiAccessKey()).toUriString();

        log.info("Getting latest currency exchange rates for default base currency EUR. URL: {}", url);
        FixerLatestRatesResponse response = fixerRestTemplate.getForObject(url, FixerLatestRatesResponse.class);
        log.info("Fixer latest rates response {}", response);

        return response;
    }

    public CurrenciesResponse getCurrencies() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerConfig.getBaseUrl() + fixerConfig.getCurrenciesUrl())
                .queryParam("access_key", fixerConfig.getApiAccessKey()).toUriString();
        log.info("Getting currencies. URL: {}", url);
        CurrenciesResponse response = fixerRestTemplate.getForObject(url, CurrenciesResponse.class);
        log.info("Fixer currencies info response {}", response);

        return response;
    }
}
