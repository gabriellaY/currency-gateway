package com.currency.gateway.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.validation.annotation.Validated;

import com.currency.gateway.model.latestexchange.LatestExchangeDto;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.LatestExchange;

import jakarta.validation.Valid;

@Mapper(componentModel = "spring")
@Validated
public interface LatestExchangeMapper {

    @Mapping(source = "exchangeCurrency.symbol", target = "currency")
    LatestExchangeDto toDto(@Valid LatestExchange latestExchange);

    default LatestExchangeDto mapCachedResponseToLatestExchange(@Valid Map<String, Object> cachedResponse) {
        long id = ((Number) cachedResponse.get("id")).longValue();

        Map<String, Object> exchangeCurrencyMap = (Map<String, Object>) cachedResponse.get("exchangeCurrency");
        Currency exchangeCurrency =
                new Currency((String) exchangeCurrencyMap.get("symbol"), (String) exchangeCurrencyMap.get("name"));
        long exchangeCurrencyId = ((Number) exchangeCurrencyMap.get("id")).longValue();
        exchangeCurrency.setId(exchangeCurrencyId);

        double rate = (Double) cachedResponse.get("rate");
        long timestamp = ((Number) cachedResponse.get("timestamp")).longValue();

        return new LatestExchangeDto(id, timestamp, exchangeCurrency.getSymbol(), rate);
    }
}
