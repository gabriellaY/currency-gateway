package com.currency.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.currency.gateway.dto.LatestExchangeDto;
import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.HistoricalExchange;
import com.currency.gateway.entity.LatestExchange;
import com.currency.gateway.mapper.LatestExchangeMapper;
import com.currency.gateway.model.HistoricalExchangeRequest;
import com.currency.gateway.model.HistoricalExchangeResponse;
import com.currency.gateway.model.LatestExchangeRequest;
import com.currency.gateway.model.LatestExchangeResponse;
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
    private ApiRequestService apiRequestService;
    
    @Mock
    private LatestExchangeMapper latestExchangeMapper;

    @InjectMocks
    private ExchangeApiService exchangeApiService;


    @Test
    public void testProcessLatestExchangeRequest() {
        LatestExchangeRequest request = new LatestExchangeRequest();
        request.setCurrency("USD");

        Currency currency = new Currency("USD", "US dollar");
        ApiRequest savedRequest = new ApiRequest();
        savedRequest.setCurrency(currency);

        Currency exchangeCurrency = new Currency("EUR", "Euro");
        LatestExchange latestExchange = new LatestExchange();
        latestExchange.setId(1L);
        latestExchange.setTimestamp(System.currentTimeMillis());
        latestExchange.setBaseCurrency(currency);
        latestExchange.setExchangeCurrency(exchangeCurrency);
        latestExchange.setRate(1.234);
        List<LatestExchange> exchangeList = new ArrayList<>();
        exchangeList.add(latestExchange);

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(latestExchangeRepository.findByBaseCurrency(savedRequest.getCurrency()))
                .thenReturn(Optional.of(exchangeList));

        LatestExchangeResponse response = exchangeApiService.processLatestExchangeRequest(request);
        List<LatestExchangeDto> exchanges = response.getLatestExchanges();

        assertNotNull(response);
        assertEquals(1, exchanges.size());
        assertEquals(latestExchange.getBaseCurrency().getSymbol(), exchanges.get(0).getCurrency());
        assertEquals(latestExchange.getRate(), exchanges.get(0).getRate());
    }

    @Test
    public void testProcessLatestExchangeRequest_NoCurrencyFound() {
        LatestExchangeRequest request = new LatestExchangeRequest();
        request.setCurrency("USD");

        Currency currency = new Currency("USD", "US dollar");
        ApiRequest savedRequest = new ApiRequest();
        savedRequest.setCurrency(currency);

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(latestExchangeRepository.findByBaseCurrency(savedRequest.getCurrency()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            exchangeApiService.processLatestExchangeRequest(request);
        });
    }

    @Test
    public void testProcessHistoryRequest() {
        int period = 24;
        long startTime = System.currentTimeMillis() - period * 3600000L;

        HistoricalExchangeRequest request = new HistoricalExchangeRequest();
        request.setCurrency("USD");
        request.setPeriod(period);
        
        ApiRequest savedRequest = new ApiRequest();
        savedRequest.setCurrency(new Currency("USD", "US dollar"));
        savedRequest.setTimestamp(System.currentTimeMillis());
        savedRequest.setPeriod(period);

        HistoricalExchange historicalExchange = new HistoricalExchange();
        historicalExchange.setTimestamp(startTime);
        historicalExchange.setRate(1.234);

        List<HistoricalExchange> historicalExchangeList = new ArrayList<>();
        historicalExchangeList.add(historicalExchange);

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(historicalExchangeRepository.findByBaseCurrencyAndTimestamp(eq(savedRequest.getCurrency().getSymbol()), anyLong()))
                .thenReturn(Optional.of(historicalExchangeList));

        HistoricalExchangeResponse response = exchangeApiService.processHistoryRequest(request);

        assertNotNull(response);
        assertEquals(savedRequest.getTimestamp(), response.getTimestamp());
        assertEquals(savedRequest.getCurrency().getSymbol(), response.getCurrency());
        assertEquals(period, response.getPeriod());

        List<HistoricalExchangeResponse.HistoricalExchangeData> exchangeHistory = response.getExchangeHistory();
        assertEquals(1, exchangeHistory.size());

        HistoricalExchangeResponse.HistoricalExchangeData data = exchangeHistory.get(0);
        assertEquals(startTime, data.getTimestamp());
        assertEquals(1.234, data.getRate());
    }

    @Test
    public void testProcessHistoryRequest_NoDataFound() {
        int period = 24;
        
        HistoricalExchangeRequest request = new HistoricalExchangeRequest();
        request.setPeriod(period);

        ApiRequest savedRequest = new ApiRequest();
        savedRequest.setCurrency(new Currency("USD", "US dollar"));
        savedRequest.setTimestamp(System.currentTimeMillis());
        savedRequest.setPeriod(period);

        when(apiRequestService.processApiRequest(request))
                .thenReturn(savedRequest);
        when(historicalExchangeRepository.findByBaseCurrencyAndTimestamp(eq(savedRequest.getCurrency().getSymbol()), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            exchangeApiService.processHistoryRequest(request);
        });
    }
}
