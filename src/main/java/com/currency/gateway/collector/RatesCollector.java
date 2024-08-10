package com.currency.gateway.collector;

import java.sql.Date;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.currency.gateway.client.FixerClient;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.exception.CurrencyNotFoundException;
import com.currency.gateway.model.CurrenciesResponse;
import com.currency.gateway.model.FixerLatestRatesResponse;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.HistoricalExchangeRepository;
import com.currency.gateway.repository.LatestExchangeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Rates collector getting current and historical exchange data and saving it to the DB.
 */
@Slf4j
@Service
public class RatesCollector {

    final FixerClient fixerClient;
    final LatestExchangeRepository latestExchangeRepository;
    final HistoricalExchangeRepository historicalExchangeRepository;
    final CurrencyRepository currencyRepository;

    @Autowired
    public RatesCollector(FixerClient fixerClient, LatestExchangeRepository latestExchangeRepository,
                          HistoricalExchangeRepository historicalExchangeRepository,
                          CurrencyRepository currencyRepository) {
        this.fixerClient = fixerClient;
        this.latestExchangeRepository = latestExchangeRepository;
        this.historicalExchangeRepository = historicalExchangeRepository;
        this.currencyRepository = currencyRepository;
    }
    
    @Scheduled(cron = "${currency-gateway.schedules.rates-collector}")
    @Transactional
    public void collectRates() {
        log.info("Collecting currency rates.");
        //First collect currency symbols to make sure all available currencies are present in the DB.
        CurrenciesResponse currenciesResponse = fixerClient.getCurrencies();
        if (currenciesResponse != null && currenciesResponse.isSuccess()) {
            saveCurrenciesToDb(currenciesResponse);
        }

        //Then collect the latest exchange rates.
        FixerLatestRatesResponse response = fixerClient.getLatestRates();

        if (response != null && response.isSuccess()) {
            saveRatesToDb(response);
        }
    }

    @Transactional
    public void saveCurrenciesToDb(CurrenciesResponse currenciesResponse) {
        HashMap<String, String> currencies = currenciesResponse.getSymbols();

        for (HashMap.Entry<String, String> c : currencies.entrySet()) {
            Currency currency = new Currency(c.getKey(), c.getValue());

            // Check if the currency is not already present in the DB, if not then add it.
            Optional<Currency> currencyInDb = currencyRepository.findBySymbol(c.getKey());

            currencyInDb.ifPresentOrElse(curr -> {
                log.info("Currency {} already present in the DB.", curr.getSymbol());
            }, () -> {
                log.info("Saving currency {} to the DB.", currency.getSymbol());
                currencyRepository.save(currency);
            });
        }
    }

    @Transactional
    public void saveRatesToDb(FixerLatestRatesResponse latestRates) {
        String baseCurrency = latestRates.getBase();
        long timestamp = latestRates.getTimestamp();
        Date date = latestRates.getDate();
        HashMap<String, Double> rates = latestRates.getRates();

        for (HashMap.Entry<String, Double> rate : rates.entrySet()) {
            //Make sure both currencies are present in the DB
            Optional<Currency> base = currencyRepository.findBySymbol(baseCurrency);
            Optional<Currency> target = currencyRepository.findBySymbol(rate.getKey());

            if (base.isEmpty()) {
                throw new CurrencyNotFoundException(String.format("Base currency (%s) not present in the DB.", baseCurrency));
            }
            if (target.isEmpty()) {
                throw new CurrencyNotFoundException(String.format("Target currency (%s) not present in the DB.", rate.getKey()));
            }

            HistoricalExchange historicalExchange =
                    new HistoricalExchange(base.get(), target.get(), rate.getValue(), timestamp, date);
            historicalExchangeRepository.save(historicalExchange);

            //Check if that exchange is already present in the LatestExchange table, if it is - update it
            Optional<LatestExchange> latestExchangeOptional =
                    latestExchangeRepository.findByBaseCurrencyAndExchangeCurrency(base.get(), target.get());

            latestExchangeOptional.ifPresentOrElse(latestExchange -> {
                LatestExchange exchange = latestExchangeOptional.get();
                exchange.setRate(rate.getValue());
                exchange.setDate(date);
                exchange.setTimestamp(timestamp);
                latestExchangeRepository.save(exchange);
            }, () -> {
                LatestExchange exchange =
                        new LatestExchange(base.get(), target.get(), rate.getValue(), timestamp, date);
                latestExchangeRepository.save(exchange);
            });
        }
    }

    //Used for testing
    public void collectRatesMock() {
        log.info("Collecting currency rates.");

        // Mocked response for CurrenciesResponse
        var currenciesResponse = new CurrenciesResponse(true,new HashMap<>() {{
            put("USD", "United States Dollar");
            put("EUR", "Euro");
            put("GBP", "British Pound Sterling");
            put("JPY", "Japanese Yen");
            put("AUD", "Australian Dollar");
            // Add more currency symbols as needed
        }});

        if (currenciesResponse.isSuccess()) {
            saveCurrenciesToDb(currenciesResponse);
        }

        // Mocked response for FixerLatestRatesResponse
        var latestRatesResponse = new FixerLatestRatesResponse();
        latestRatesResponse.setBase("USD");
        latestRatesResponse.setTimestamp(System.currentTimeMillis() / 1000L);
        latestRatesResponse.setDate(new Date(System.currentTimeMillis()));
        latestRatesResponse.setRates(new HashMap<>() {{
            put("EUR", 0.85);
            put("GBP", 0.75);
            put("JPY", 110.0);
            put("AUD", 1.35);
            // Add more rates as needed
        }});
        latestRatesResponse.setSuccess(true);

        if (latestRatesResponse.isSuccess()) {
            saveRatesToDb(latestRatesResponse);
        }
    }
}