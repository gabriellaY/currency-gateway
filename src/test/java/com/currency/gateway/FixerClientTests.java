package com.currency.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.sql.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.currency.gateway.client.FixerClient;
import com.currency.gateway.configuration.FixerConfig;
import com.currency.gateway.model.CurrenciesResponse;
import com.currency.gateway.model.FixerLatestRatesResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FixerClientTests {
    @Mock
    private FixerConfig fixerConfig;
    
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FixerClient fixerClient;

    @BeforeEach
    void setUp() {
        when(fixerConfig.getBaseUrl()).thenReturn("http://data.fixer.io/api");
        when(fixerConfig.getLatestRatesUrl()).thenReturn("/latest");
        when(fixerConfig.getCurrenciesUrl()).thenReturn("/symbols");
        when(fixerConfig.getApiAccessKey()).thenReturn("access_key");
    }

    @Test
    void getLatestRatesTest() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerConfig.getBaseUrl() + fixerConfig.getLatestRatesUrl())
                .queryParam("access_key", fixerConfig.getApiAccessKey())
                .toUriString();

        HashMap<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.1);
        FixerLatestRatesResponse expectedResponse =
                new FixerLatestRatesResponse(true, 123456789, "EUR",
                                             new Date(System.currentTimeMillis()), rates);
        when(restTemplate.getForObject(url, FixerLatestRatesResponse.class)).thenReturn(expectedResponse);

        FixerLatestRatesResponse response = fixerClient.getLatestRates();

        assertNotNull(response);
        verify(restTemplate, times(1)).getForObject(url, FixerLatestRatesResponse.class);
    }

    @Test
    void getCurrenciesTest() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerConfig.getBaseUrl() + fixerConfig.getCurrenciesUrl())
                .queryParam("access_key", fixerConfig.getApiAccessKey())
                .toUriString();

        HashMap<String, String> currencies = new HashMap<>();
        currencies.put("USD", "United States Dollar");
        currencies.put("EUR", "Euro");
        CurrenciesResponse expectedResponse = new CurrenciesResponse(true, currencies);
        when(restTemplate.getForObject(url, CurrenciesResponse.class)).thenReturn(expectedResponse);

        CurrenciesResponse response = fixerClient.getCurrencies();

        assertNotNull(response);
        verify(restTemplate, times(1)).getForObject(url, CurrenciesResponse.class);
    }
}
