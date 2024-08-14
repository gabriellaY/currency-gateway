package com.currency.gateway.collector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.currency.gateway.service.CacheService;

import lombok.extern.slf4j.Slf4j;

/**
 * Rates collector getting current and historical exchange data and saving it to the DB.
 */
@Slf4j
@Service
public class RatesCollector {

    private final FixerClient fixerClient;
    private final LatestExchangeRepository latestExchangeRepository;
    private final HistoricalExchangeRepository historicalExchangeRepository;
    private final CurrencyRepository currencyRepository;
    private final CacheService cacheService;

    @Autowired
    public RatesCollector(FixerClient fixerClient, LatestExchangeRepository latestExchangeRepository,
                          HistoricalExchangeRepository historicalExchangeRepository,
                          CurrencyRepository currencyRepository, CacheService cacheService) {
        this.fixerClient = fixerClient;
        this.latestExchangeRepository = latestExchangeRepository;
        this.historicalExchangeRepository = historicalExchangeRepository;
        this.currencyRepository = currencyRepository;
        this.cacheService = cacheService;
    }

    @Scheduled(cron = "${currency-gateway.schedules.rates-collector}")
    @Transactional
    public void collectRates() {
        log.info("Collecting currency rates.");
        CurrenciesResponse currenciesResponse = fixerClient.getCurrencies();
        if (currenciesResponse != null && currenciesResponse.isSuccess()) {
            saveCurrenciesToDb(currenciesResponse);
        }

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
        long timestamp = latestRates.getTimestamp();
        Date date = latestRates.getDate();
        HashMap<String, Double> ratesBasedOnEuro = latestRates.getRates();
        HashMap<String, HashMap<String, Double>> crossRates = new HashMap<>();

        for (String baseCurrency : ratesBasedOnEuro.keySet()) {
            HashMap<String, Double> rates = new HashMap<>();
            Double baseRate = ratesBasedOnEuro.get(baseCurrency);

            log.info("Calculate cross rate for base currency {}.", baseCurrency);

            for (String exchangeCurrency : ratesBasedOnEuro.keySet()) {
                Double exchangeRate = ratesBasedOnEuro.get(exchangeCurrency);
                if (exchangeRate != 0) {
                    double crossRate = baseRate / exchangeRate;

                    BigDecimal roundedCrossRate = new BigDecimal(crossRate).setScale(4, RoundingMode.HALF_UP);
                    rates.put(exchangeCurrency, roundedCrossRate.doubleValue());
                } else {
                    log.warn("Exchange rate for {} is zero, skipping..", exchangeCurrency);
                }
            }
            crossRates.put(baseCurrency, rates);
            saveRates(crossRates, baseCurrency, timestamp, date);

            crossRates.clear();
        }
    }

    private void saveRates(HashMap<String, HashMap<String, Double>> crossRates, String baseCurrency, long timestamp, Date date) {
        log.info("Saving exchange rates for base currency {}", baseCurrency);
        List<HistoricalExchange> historicalExchanges = new ArrayList<>();
        List<LatestExchange> latestExchanges = new ArrayList<>();

        Optional<Currency> base = currencyRepository.findBySymbol(baseCurrency);
        HashMap<String, Double> rates = crossRates.get(baseCurrency);

        for (HashMap.Entry<String, Double> rate : rates.entrySet()) {
            Optional<Currency> target = currencyRepository.findBySymbol(rate.getKey());

            if (base.isEmpty()) {
                throw new CurrencyNotFoundException(
                        String.format("Base currency (%s) not present in the DB.", baseCurrency));
            }
            if (target.isEmpty()) {
                throw new CurrencyNotFoundException(
                        String.format("Target currency (%s) not present in the DB.", rate.getKey()));
            }

            HistoricalExchange historicalExchange =
                    new HistoricalExchange(base.get(), target.get(), rate.getValue(), timestamp, date);
            historicalExchanges.add(historicalExchange);
            
            Optional<LatestExchange> latestExchangeOptional =
                    latestExchangeRepository.findByBaseCurrencyAndExchangeCurrency(base.get(), target.get());

            latestExchangeOptional.ifPresentOrElse(latestExchange -> {
                LatestExchange exchange = latestExchangeOptional.get();
                exchange.setRate(rate.getValue());
                exchange.setDate(date);
                exchange.setTimestamp(timestamp);
                
                latestExchanges.add(exchange);
            }, () -> {
                LatestExchange exchange =
                        new LatestExchange(base.get(), target.get(), rate.getValue(), timestamp, date);
                latestExchanges.add(exchange);
            });
        }

        historicalExchangeRepository.saveAll(historicalExchanges);
        latestExchangeRepository.saveAll(latestExchanges);

        saveLatestExchangesToCache(base.get());
    }

    private void saveLatestExchangesToCache(Currency baseCurrency) {
        log.info("Saving latest exchanges for base currency {} to the cache", baseCurrency.getSymbol());
        Optional<List<LatestExchange>> latestExchange = latestExchangeRepository.findByBaseCurrency(baseCurrency);
        latestExchange.ifPresent(
                latestExchanges -> cacheService.cacheLatestExchanges(baseCurrency.getSymbol(), latestExchanges));
    }
}