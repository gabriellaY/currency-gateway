package com.currency.gateway;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.currency.gateway.client.FixerClient;
import com.currency.gateway.collector.RatesCollector;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.model.CurrenciesResponse;
import com.currency.gateway.model.FixerLatestRatesResponse;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.HistoricalExchangeRepository;
import com.currency.gateway.repository.LatestExchangeRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RatesCollectorTests {
    @InjectMocks
    private RatesCollector ratesCollector;

    @Mock
    private FixerClient fixerClient;

    @Mock
    private LatestExchangeRepository latestExchangeRepository;

    @Mock
    private HistoricalExchangeRepository historicalExchangeRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Test
    void testCollectRatesSuccess() {
        CurrenciesResponse currenciesResponse = new CurrenciesResponse();
        currenciesResponse.setSuccess(true);
        currenciesResponse.setSymbols(new HashMap<>() {{
            put("USD", "United States Dollar");
            put("EUR", "Euro");
        }});

        FixerLatestRatesResponse latestRatesResponse = new FixerLatestRatesResponse();
        latestRatesResponse.setSuccess(true);
        latestRatesResponse.setBase("EUR");
        latestRatesResponse.setTimestamp(123456789);
        latestRatesResponse.setDate(new Date(System.currentTimeMillis()));
        latestRatesResponse.setRates(new HashMap<>() {{
            put("USD", 1.1);
        }});

        when(fixerClient.getCurrencies()).thenReturn(currenciesResponse);
        when(fixerClient.getLatestRates()).thenReturn(latestRatesResponse);

        Currency eur = new Currency();
        eur.setName("Euro");
        eur.setSymbol("EUR");
        Currency usd = new Currency();
        usd.setName("United States Dollar");
        usd.setSymbol("USD");

        when(currencyRepository.findBySymbol("EUR")).thenReturn(Optional.of(eur));
        when(currencyRepository.findBySymbol("USD")).thenReturn(Optional.of(usd));

        when(latestExchangeRepository.findByBaseCurrencyAndExchangeCurrency(eur, usd)).thenReturn(Optional.empty());

        ratesCollector.collectRates();

        verify(currencyRepository, times(2)).save(any(Currency.class));
        verify(historicalExchangeRepository, times(1)).save(any(HistoricalExchange.class));
        verify(latestExchangeRepository, times(1)).save(any(LatestExchange.class));
    }

    @Test
    void testCollectRatesCurrencyNotFound() {
        CurrenciesResponse currenciesResponse = new CurrenciesResponse();
        currenciesResponse.setSuccess(true);
        currenciesResponse.setSymbols(new HashMap<>() {{
            put("USD", "United States Dollar");
            put("EUR", "Euro");
        }});

        FixerLatestRatesResponse latestRatesResponse = new FixerLatestRatesResponse();
        latestRatesResponse.setSuccess(true);
        latestRatesResponse.setBase("EUR");
        latestRatesResponse.setTimestamp(123456789);
        latestRatesResponse.setDate(new Date(System.currentTimeMillis()));
        latestRatesResponse.setRates(new HashMap<>() {{
            put("USD", 1.1);
        }});

        when(fixerClient.getCurrencies()).thenReturn(currenciesResponse);
        when(fixerClient.getLatestRates()).thenReturn(latestRatesResponse);

        when(currencyRepository.findBySymbol("EUR")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ratesCollector.collectRates();
        });

        assertEquals("Base currency (EUR) not present in the DB.", exception.getMessage());
    }
}
