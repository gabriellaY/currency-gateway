package com.currency.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.currency.gateway.mapper.HistoricalExchangeMapper;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeDto;
import com.currency.gateway.model.latestexchange.LatestExchangeDto;
import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.exception.CurrencyNotFoundException;
import com.currency.gateway.exception.ExchangeDataNotFoundException;
import com.currency.gateway.mapper.LatestExchangeMapper;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeRequest;
import com.currency.gateway.model.historicalexchange.HistoricalExchangeResponse;
import com.currency.gateway.model.latestexchange.LatestExchangeRequest;
import com.currency.gateway.model.latestexchange.LatestExchangeResponse;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.HistoricalExchangeRepository;
import com.currency.gateway.repository.LatestExchangeRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ExchangeApiServiceTests {

    @Mock
    private LatestExchangeRepository latestExchangeRepository;

    @Mock
    private HistoricalExchangeRepository historicalExchangeRepository;
    
    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ApiRequestService apiRequestService;

    @Mock
    private LatestExchangeMapper latestExchangeMapper;
    
    @Mock
    private HistoricalExchangeMapper historicalExchangeMapper;
    
    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ExchangeApiService exchangeApiService;


    @Test
    public void testProcessLatestExchangeRequest() {
        LatestExchangeRequest request = new LatestExchangeRequest();
        request.setCurrency("USD");
        Currency currency = new Currency("USD", "US dollar");
        ApiRequest savedRequest = new ApiRequest();
        
        LatestExchange latestExchange = getLatestExchange();
        List<LatestExchange> exchangeList = new ArrayList<>();
        exchangeList.add(latestExchange);

        LatestExchangeDto latestExchangeDto =
                new LatestExchangeDto(latestExchange.getId(), latestExchange.getTimestamp(),
                                      latestExchange.getExchangeCurrency().getSymbol(), latestExchange.getRate());

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(latestExchangeRepository.findByBaseCurrency(currency))
                .thenReturn(Optional.of(exchangeList));
        when(currencyRepository.findBySymbol(request.getCurrency()))
                .thenReturn(Optional.of(currency));
        when(latestExchangeMapper.toDto(latestExchange))
                .thenReturn(latestExchangeDto);

        ArrayList<Map<String, Object>> cachedResponse = getLatestExchangeCachedResponse();

        when(cacheService.getLatestExchanges(currency.getSymbol())).thenReturn(cachedResponse);
        when(latestExchangeMapper.mapCachedResponseToLatestExchange(cachedResponse.get(0)))
                .thenReturn(latestExchangeDto);
        
        LatestExchangeResponse response = exchangeApiService.processLatestExchangeRequest(request);
        List<LatestExchangeDto> exchanges = response.getLatestExchanges();

        assertNotNull(response);
        assertEquals(1, exchanges.size());
        assertEquals(latestExchange.getExchangeCurrency().getSymbol(), exchanges.get(0).getCurrency());
        assertEquals(latestExchange.getRate(), exchanges.get(0).getRate());
    }

    @Test
    public void testProcessLatestExchangeRequestNoCurrencyFound() {
        LatestExchangeRequest request = new LatestExchangeRequest();
        request.setCurrency("USD");

        Currency currency = new Currency("USD", "US dollar");
        ApiRequest savedRequest = new ApiRequest();

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(latestExchangeRepository.findByBaseCurrency(currency))
                .thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeApiService.processLatestExchangeRequest(request);
        });
    }

    @Test
    public void testProcessHistoryRequest() {
        int period = 24;
        long startTime = System.currentTimeMillis() - period * 3600000L;
        long requestTimestamp = System.currentTimeMillis();

        Currency currency = new Currency("USD", "US dollar");
        
        HistoricalExchangeRequest request = new HistoricalExchangeRequest();
        request.setCurrency(currency.getSymbol());
        request.setPeriod(period);
        request.setTimestamp(requestTimestamp);
        
        ApiRequest savedRequest = new ApiRequest();
        savedRequest.setTimestamp(requestTimestamp);

        HistoricalExchange historicalExchange = new HistoricalExchange();
        historicalExchange.setTimestamp(startTime);
        historicalExchange.setRate(1.234);
        historicalExchange.setExchangeCurrency(new Currency("EUR", "Euro"));

        List<HistoricalExchange> historicalExchangeList = new ArrayList<>();
        historicalExchangeList.add(historicalExchange);
        
        HistoricalExchangeDto historicalExchangeDto = new HistoricalExchangeDto(startTime, 1.234, "EUR");

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(historicalExchangeRepository.findByBaseCurrencyAndTimestamp(anyString(), anyLong()))
                .thenReturn(Optional.of(historicalExchangeList));
        when(historicalExchangeMapper.toDto(historicalExchange))
                .thenReturn(historicalExchangeDto);

        HistoricalExchangeResponse response = exchangeApiService.processHistoryRequest(request);

        assertNotNull(response);
        assertEquals(savedRequest.getTimestamp(), response.getTimestamp());
        assertEquals("USD", response.getCurrency());
        assertEquals(period, response.getPeriod());

        List<HistoricalExchangeDto> exchangeHistory = response.getExchangeHistory();
        assertEquals(1, exchangeHistory.size());

        HistoricalExchangeDto data = exchangeHistory.get(0);
        assertEquals(startTime, data.getTimestamp());
        assertEquals(1.234, data.getRate());
    }

    @Test
    public void testProcessHistoryRequestNoDataFound() {
        int period = 24;

        HistoricalExchangeRequest request = new HistoricalExchangeRequest();
        request.setPeriod(period);

        ApiRequest savedRequest = new ApiRequest();
        Currency currency = new Currency("USD", "US dollar");
        savedRequest.setTimestamp(System.currentTimeMillis());

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(historicalExchangeRepository.findByBaseCurrencyAndTimestamp(eq(currency.getSymbol()),
                                                                         anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ExchangeDataNotFoundException.class, () -> {
            exchangeApiService.processHistoryRequest(request);
        });
    }
    
    private LatestExchange getLatestExchange() {
        Currency currency = new Currency("USD", "US dollar");
        Currency exchangeCurrency = new Currency("EUR", "Euro");

        LatestExchange latestExchange = new LatestExchange();
        latestExchange.setId(1L);
        latestExchange.setTimestamp(System.currentTimeMillis());
        latestExchange.setBaseCurrency(currency);
        latestExchange.setExchangeCurrency(exchangeCurrency);
        latestExchange.setRate(1.234);

        List<LatestExchange> exchangeList = new ArrayList<>();
        exchangeList.add(latestExchange);
        
        return latestExchange;
    }
    private ArrayList<Map<String, Object>> getLatestExchangeCachedResponse() {
        ArrayList<Map<String, Object>> cachedResponse = new ArrayList<>();
        Map<String, Object> exchangeData = new LinkedHashMap<>();

        exchangeData.put("id", 11111);
        exchangeData.put("rate", 1.2345);
        exchangeData.put("timestamp", 1625097600);

        Map<String, Object> exchangeCurrencyMap = new LinkedHashMap<>();
        exchangeCurrencyMap.put("symbol", "EUR");
        exchangeCurrencyMap.put("name", "Euro");
        exchangeCurrencyMap.put("id", 12345678);

        exchangeData.put("exchangeCurrency", exchangeCurrencyMap);
        cachedResponse.add(exchangeData);
        
        return cachedResponse;
    }
}
