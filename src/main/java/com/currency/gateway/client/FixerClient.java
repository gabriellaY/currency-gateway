package com.currency.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    @Value("${currency-gateway.fixer.base-url}")
    private String fixerBaseUrl;

    @Value("${currency-gateway.fixer.latest-rates-url}")
    private String latestRatesUrl;

    @Value("${currency-gateway.fixer.currencies-url}")
    private String currenciesUrl;

    @Value("${currency-gateway.fixer.api-access-key}")
    private String apiAccesKey;

    public FixerLatestRatesResponse getLatestRates() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerBaseUrl + latestRatesUrl)
                        .queryParam("access_key", apiAccesKey)
                        .toUriString();
        log.info("Getting latest currency exchange rates. URL: {}", url);

        return fixerRestTemplate.getForObject(url, FixerLatestRatesResponse.class);
    }

    public CurrenciesResponse getCurrencies() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerBaseUrl + currenciesUrl)
                        .queryParam("access_key", apiAccesKey)
                        .toUriString();
        log.info("Getting currencies. URL: {}", url);

        return fixerRestTemplate.getForObject(url, CurrenciesResponse.class);
    }

}
