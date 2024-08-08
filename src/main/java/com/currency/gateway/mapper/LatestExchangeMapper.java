package com.currency.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.currency.gateway.dto.LatestExchangeDto;
import com.currency.gateway.entity.LatestExchange;

@Mapper(componentModel = "spring")
public interface LatestExchangeMapper {

    @Mapping(source = "exchangeCurrency.symbol", target = "currency")
    LatestExchangeDto toDto(LatestExchange latestExchange);
}
