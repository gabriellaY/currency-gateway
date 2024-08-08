package com.currency.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "currency-gateway.fixer")
@Component
public class FixerConfig {

    private String baseUrl;
    private String latestRatesUrl;
    private String currenciesUrl;
    private String apiAccessKey;
}
