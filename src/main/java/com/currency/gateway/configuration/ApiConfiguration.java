package com.currency.gateway.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfiguration {
    @Bean
    @Qualifier("fixer")
    public RestTemplate fixerRestTemplate() {
        return new RestTemplate();
    }
}
