package com.currency.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.validation.annotation.Validated;

import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeDto;

import jakarta.validation.Valid;

@Mapper(componentModel = "spring")
@Validated
public interface HistoricalExchangeMapper {

    @Mapping(source = "exchangeCurrency.symbol", target = "exchangeCurrency")
    HistoricalExchangeDto toDto(@Valid HistoricalExchange historicalExchange);
}
