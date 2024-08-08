package com.currency.gateway.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;

@Repository
public interface HistoricalExchangeRepository extends JpaRepository<HistoricalExchange, Long> {

    Optional<HistoricalExchange> findByBaseCurrency(Currency baseCurrency);

    @Query("SELECT he FROM HistoricalExchange he WHERE he.baseCurrency.symbol = :baseCurrency AND he.timestamp >= :timestamp")
    Optional<List<HistoricalExchange>> findByBaseCurrencyAndTimestamp(@Param("baseCurrency") String baseCurrency,
                                                                      @Param("timestamp") long timestamp);
}
