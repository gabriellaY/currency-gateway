package com.currency.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.currency.gateway.collector.StatisticsCollector;
import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.entity.Service;
import com.currency.gateway.model.ExchangeApiRequest;
import com.currency.gateway.model.latestexchange.LatestExchangeRequest;
import com.currency.gateway.repository.ApiRequestRepository;
import com.currency.gateway.repository.CurrencyRepository;
import com.currency.gateway.repository.ServiceRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ApiRequestServiceTests {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ApiRequestRepository apiRequestRepository;
    
    @Mock
    private ServiceRepository serviceRepository;
    
    @Mock
    private StatisticsCollector statisticsCollector;

    @InjectMocks
    private ApiRequestService apiRequestService;

    @Test
    void testIsDuplicate() {
        String requestId = UUID.randomUUID().toString();

        boolean isDuplicate = apiRequestService.isDuplicate(requestId);
        assertFalse(isDuplicate);

        isDuplicate = apiRequestService.isDuplicate(requestId);
        assertTrue(isDuplicate);
    }

    @Test
    void testProcessApiRequest() {
        LatestExchangeRequest request = new LatestExchangeRequest();
        Service service = new Service();
        service.setName("EXT_SERVICE_1");

        Currency currency = new Currency();
        currency.setSymbol("EUR");
        currency.setName("Euro");
        
        request.setRequestId(UUID.randomUUID().toString());
        request.setService(service.getName());
        request.setClient("1234");
        request.setCurrency(currency.getSymbol());
        request.setTimestamp(System.currentTimeMillis());
        
        ApiRequest savedApiRequest = new ApiRequest();
        savedApiRequest.setService(service);
        savedApiRequest.setTimestamp(request.getTimestamp());
        savedApiRequest.setEndUserID(request.getClient());
        savedApiRequest.setId(request.getRequestId());

        when(currencyRepository.findBySymbol(request.getCurrency())).thenReturn(Optional.of(currency));
        when(apiRequestRepository.save(any(ApiRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(serviceRepository.findByName(anyString())).thenReturn(Optional.of(new Service()));
        when(statisticsCollector.saveApiRequestStatistics(any(ApiRequest.class))).thenReturn(savedApiRequest);

        ApiRequest savedRequest = apiRequestService.processApiRequest(request);

        assertNotNull(savedRequest);
        assertEquals(request.getRequestId(), savedRequest.getId());
        assertEquals(request.getClient(), savedRequest.getEndUserID());
        assertEquals(request.getTimestamp(), savedRequest.getTimestamp());

        verify(currencyRepository, times(1)).findBySymbol("EUR");
    }

    @Test
    void testProcessApiRequestCurrencyNotFound() {
        ExchangeApiRequest request = new LatestExchangeRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClient("1234");
        request.setCurrency("EUR");
        request.setTimestamp(System.currentTimeMillis());

        when(currencyRepository.findBySymbol("EUR")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            apiRequestService.processApiRequest(request);
        });

        assertEquals("No such currency present in the DB.", exception.getMessage());

        verify(currencyRepository, times(1)).findBySymbol("EUR");
        verify(apiRequestRepository, times(0)).save(any(ApiRequest.class));
    }
}
