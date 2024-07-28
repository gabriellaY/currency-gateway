package com.currency.gateway.collector;

import java.sql.Date;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.currency.gateway.client.FixerClient;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.model.CurrenciesResponse;
import com.currency.gateway.model.FixerLatestRatesResponse;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.HistoricalExchangeRepository;
import com.currency.gateway.repository.LatestExchangeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Rates collector getting current and historical exchange data and saving it to the DB.
 */
@Component
@Slf4j
public class RatesCollector {

    @Autowired
    FixerClient fixerClient;

    @Autowired
    LatestExchangeRepository latestExchangeRepository;

    @Autowired
    HistoricalExchangeRepository historicalExchangeRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Scheduled(cron = "${currency-gateway.schedules.rates-collector}")
    public void collectRates() {
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

    private void saveCurrenciesToDb(CurrenciesResponse currenciesResponse) {
        HashMap<String, String> currencies = currenciesResponse.getSymbols();

        for (HashMap.Entry<String, String> c : currencies.entrySet()) {
            Currency currency = new Currency();
            currency.setSymbol(c.getKey());
            currency.setName(c.getValue());

            currencyRepository.save(currency);
        }
    }

    private void saveRatesToDb(FixerLatestRatesResponse latestRates) {
        String baseCurrency = latestRates.getBase();
        long timestamp = latestRates.getTimestamp();
        Date date = latestRates.getDate();
        HashMap<String, Double> rates = latestRates.getRates();

        for (HashMap.Entry<String, Double> rate : rates.entrySet()) {
            //Make sure both currencies are present in the DB
            Optional<Currency> base = currencyRepository.findBySymbol(baseCurrency);
            Optional<Currency> target = currencyRepository.findBySymbol(rate.getKey());

            if (base.isEmpty()) {
                throw new RuntimeException(String.format("Base currency (%s) not present in the DB.", baseCurrency));
            }
            if (target.isEmpty()) {
                throw new RuntimeException(String.format("Target currency (%s) not present in the DB.", rate.getKey()));
            }

            HistoricalExchange historicalExchange = new HistoricalExchange();
            historicalExchange.setBaseCurrency(base.get());
            historicalExchange.setExchangeCurrency(target.get());
            historicalExchange.setRate(rate.getValue());
            historicalExchange.setTimestamp(timestamp);
            historicalExchange.setDate(date);
            historicalExchangeRepository.save(historicalExchange);

            //Check if that exchange is already present in the LatestExchange table, if it is - update it
            Optional<LatestExchange> latestExchangeOptional =
                    latestExchangeRepository.findByBaseCurrencyAndExchangeCurrency(base.get(), target.get());
            if (latestExchangeOptional.isEmpty()) {
                LatestExchange exchange = new LatestExchange();
                exchange.setBaseCurrency(base.get());
                exchange.setExchangeCurrency(target.get());
                exchange.setRate(rate.getValue());
                exchange.setTimestamp(timestamp);
                exchange.setDate(date);
                latestExchangeRepository.save(exchange);
            } else {
                LatestExchange latestExchange = latestExchangeOptional.get();
                latestExchange.setRate(rate.getValue());
                latestExchange.setDate(date);
                latestExchange.setTimestamp(timestamp);
                latestExchangeRepository.save(latestExchange);
            }
        }
    }
}