package com.currency.gateway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;

@Repository
public interface HistoricalExchangeRepository extends JpaRepository<HistoricalExchange, Long> {
}
