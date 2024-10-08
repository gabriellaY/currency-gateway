package com.currency.gateway.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.LatestExchange;

@Repository
public interface LatestExchangeRepository extends JpaRepository<LatestExchange, Long> {

    Optional<LatestExchange> findByBaseCurrencyAndExchangeCurrency(Currency baseCurrency, Currency exchangeCurrency);

    Optional<List<LatestExchange>> findByBaseCurrency(Currency baseCurrency);
}
