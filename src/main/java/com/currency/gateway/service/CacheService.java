package com.currency.gateway.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.model.ExchangeApiRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to handle caching.
 */
@Service
@Slf4j
public class CacheService {

    private static final String API_REQUEST_KEY = "exchange_api_request_";
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheLatestExchanges(String baseCurrency, List<LatestExchange> latestExchange) {
        redisTemplate.opsForValue().set(baseCurrency, latestExchange);
    }

    public Object getLatestExchanges(String currencySymbol) {
        return redisTemplate.opsForValue().get(currencySymbol);
    }

    public boolean isDuplicateApiRequest(ExchangeApiRequest apiRequest) {
        String key = API_REQUEST_KEY + apiRequest.getRequestId();
        if (redisTemplate.hasKey(key)) {
            return true;
        } else {
            redisTemplate.opsForValue().set(key, apiRequest, 1, TimeUnit.HOURS);
            return false;
        }
    }
}
