package com.currency.gateway.configuration;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.currency.gateway.mapper.LatestExchangeMapper;

@Configuration
public class MapperConfig {

    @Bean
    public LatestExchangeMapper latestExchangeMapper() {
        return Mappers.getMapper(LatestExchangeMapper.class);
    }
    
}
