package com.currency.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.entity.Service;
import com.currency.gateway.model.latestexchange.LatestExchangeRequest;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CacheServiceTests {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cacheService = new CacheService(redisTemplate);
    }

    @Test
    public void testCacheLatestExchanges() {
        String baseSymbol = "USD";
        List<LatestExchange> latestExchanges = getLatestExchangesList();

        cacheService.cacheLatestExchanges(baseSymbol, latestExchanges);

        verify(valueOperations, times(1)).set(baseSymbol, latestExchanges);
    }

    @Test
    public void testGetLatestExchanges() {
        String baseSymbol = "USD";
        List<LatestExchange> expectedExchanges = getLatestExchangesList();

        when(valueOperations.get(baseSymbol)).thenReturn(expectedExchanges);

        Object actualExchanges = cacheService.getLatestExchanges(baseSymbol);

        assertEquals(expectedExchanges, actualExchanges);
    }

    @Test
    public void testIsDuplicateApiRequestDuplicate() {
        LatestExchangeRequest request = getLatestExchangeRequest();
        String key = "exchange_api_request_" + request.getRequestId();

        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean isDuplicate = cacheService.isDuplicateApiRequest(request);

        assertTrue(isDuplicate);
        verify(valueOperations, never())
                .set(eq(key), eq(request), anyLong(), eq(TimeUnit.HOURS));
    }

    @Test
    public void testIsDuplicateApiRequestNewRequest() {
        LatestExchangeRequest request = getLatestExchangeRequest();
        String key = "exchange_api_request_" + request.getRequestId();

        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean isDuplicate = cacheService.isDuplicateApiRequest(request);

        assertFalse(isDuplicate);
        verify(valueOperations, times(1))
                .set(eq(key), eq(request), eq(1L), eq(TimeUnit.HOURS));
    }

    private List<LatestExchange> getLatestExchangesList() {
        Currency baseCurrency = new Currency("USD", "United States Dollar ");
        Currency exchangeCurrencyEur = new Currency("EUR", "Euro");
        Currency exchangeCurrencyGbp = new Currency("GBP", "British Pound Sterling");

        LatestExchange exchange1 =
                new LatestExchange(baseCurrency, exchangeCurrencyEur, 1.0923, 
                                   1723537863, new Date(1723537863));
        LatestExchange exchange2 =
                new LatestExchange(baseCurrency, exchangeCurrencyGbp, 1.28, 
                                   1723537863, new Date(1723537863));
        List<LatestExchange> latestExchanges = Arrays.asList(exchange1, exchange2);

        return latestExchanges;
    }

    private LatestExchangeRequest getLatestExchangeRequest() {
        Service service = new Service();
        service.setName("EXT_SERVICE_1");

        Currency currency = new Currency();
        currency.setSymbol("EUR");
        currency.setName("Euro");

        LatestExchangeRequest request = new LatestExchangeRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setService(service.getName());
        request.setClient("1234");
        request.setCurrency(currency.getSymbol());
        request.setTimestamp(System.currentTimeMillis());

        return request;
    }

}
