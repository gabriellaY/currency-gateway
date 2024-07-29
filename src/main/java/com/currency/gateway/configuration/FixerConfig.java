package com.currency.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "currency-gateway.fixer")
@Data
public class FixerConfig {

    private String baseUrl;
    private String latestRatesUrl;
    private String currenciesUrl;
    private String apiAccessKey;
}
