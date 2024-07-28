package com.currency.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.currency.gateway.client.FixerClient;
import com.currency.gateway.model.CurrenciesResponse;
import com.currency.gateway.model.FixerLatestRatesResponse;

@ExtendWith(SpringExtension.class)
public class FixerClientTests {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FixerClient fixerClient;

    private final String fixerBaseUrl = "http://data.fixer.io/api";
    private final String latestRatesUrl = "/latest";
    private final String currenciesUrl = "/symbols";
    private final String apiAccessKey = "test_access_key";

    @BeforeEach
    void setUp() {
        setField(fixerClient, "fixerBaseUrl", fixerBaseUrl);
        setField(fixerClient, "latestRatesUrl", latestRatesUrl);
        setField(fixerClient, "currenciesUrl", currenciesUrl);
        setField(fixerClient, "apiAccesKey", apiAccessKey);
    }

    @Test
    void getLatestRatesTest() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerBaseUrl + latestRatesUrl)
                .queryParam("access_key", apiAccessKey)
                .toUriString();

        FixerLatestRatesResponse expectedResponse = new FixerLatestRatesResponse();
        when(restTemplate.getForObject(url, FixerLatestRatesResponse.class)).thenReturn(expectedResponse);

        FixerLatestRatesResponse response = fixerClient.getLatestRates();

        assertNotNull(response);
        verify(restTemplate, times(1)).getForObject(url, FixerLatestRatesResponse.class);
    }

    @Test
    void getCurrenciesTest() {
        String url = UriComponentsBuilder.fromHttpUrl(fixerBaseUrl + currenciesUrl)
                .queryParam("access_key", apiAccessKey)
                .toUriString();

        CurrenciesResponse expectedResponse = new CurrenciesResponse();
        when(restTemplate.getForObject(url, CurrenciesResponse.class)).thenReturn(expectedResponse);

        CurrenciesResponse response = fixerClient.getCurrencies();

        assertNotNull(response);
        verify(restTemplate, times(1)).getForObject(url, CurrenciesResponse.class);
    }
}
