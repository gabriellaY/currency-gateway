package com.currency.gateway.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.currency.gateway.client.FixerClient;
import com.currency.gateway.configuration.FixerConfig;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.exception.CurrencyNotFoundException;
import com.currency.gateway.model.CurrenciesResponse;
import com.currency.gateway.model.FixerLatestRatesResponse;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.HistoricalExchangeRepository;
import com.currency.gateway.repository.LatestExchangeRepository;
import com.currency.gateway.service.CacheService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {FixerConfig.class})
public class RatesCollectorTests {

    @Mock
    private FixerClient fixerClient;

    @Mock
    private LatestExchangeRepository latestExchangeRepository;

    @Mock
    private HistoricalExchangeRepository historicalExchangeRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private RatesCollector ratesCollector;

    @BeforeEach
    void setUp() {
        ratesCollector = new RatesCollector(fixerClient, latestExchangeRepository, historicalExchangeRepository,
                                            currencyRepository, cacheService);
    }

    @Test
    void testCollectRatesSuccess() {
        HashMap<String, String> currencies = new HashMap<>();
        currencies.put("USD", "United States Dollar");
        currencies.put("EUR", "Euro");
        CurrenciesResponse currenciesResponse = new CurrenciesResponse(true, currencies);

        HashMap<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.1);
        FixerLatestRatesResponse latestRatesResponse =
                new FixerLatestRatesResponse(true, 123456789, "EUR",
                                             new Date(System.currentTimeMillis()), rates);

        when(fixerClient.getCurrencies()).thenReturn(currenciesResponse);
        when(fixerClient.getLatestRates()).thenReturn(latestRatesResponse);

        Currency eur = new Currency("EUR", "Euro");
        Currency usd = new Currency("USD", "United States Dollar");

        when(currencyRepository.findBySymbol("EUR")).thenReturn(Optional.of(eur));
        when(currencyRepository.findBySymbol("USD")).thenReturn(Optional.of(usd));
        when(latestExchangeRepository.findByBaseCurrencyAndExchangeCurrency(eur, usd)).thenReturn(Optional.empty());

        ratesCollector.collectRates();

        verify(historicalExchangeRepository, times(1)).saveAll(any(List.class));
        verify(latestExchangeRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void testCollectRatesCurrencyNotFound() {
        HashMap<String, String> currencies = new HashMap<>();
        currencies.put("USD", "United States Dollar");
        currencies.put("EUR", "Euro");
        CurrenciesResponse currenciesResponse = new CurrenciesResponse(true, currencies);

        HashMap<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.1);
        FixerLatestRatesResponse latestRatesResponse =
                new FixerLatestRatesResponse(true, 123456789, "EUR",
                                             new Date(System.currentTimeMillis()), rates);

        when(fixerClient.getCurrencies()).thenReturn(currenciesResponse);
        when(fixerClient.getLatestRates()).thenReturn(latestRatesResponse);

        when(currencyRepository.findBySymbol("USD")).thenReturn(Optional.empty());

        Exception exception = assertThrows(CurrencyNotFoundException.class, () -> {
            ratesCollector.collectRates();
        });

        assertEquals("Base currency (USD) not present in the DB.", exception.getMessage());
    }
}
