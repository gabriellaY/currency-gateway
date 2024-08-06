package com.currency.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.Currency;
import com.currency.gateway.model.ExchangeApiRequest;
import com.currency.gateway.model.LatestExchangeRequest;
import com.currency.gateway.repository.ApiRequestRepository;
import com.currency.gateway.repository.CurrencyRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ApiRequestServiceTests {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ApiRequestRepository apiRequestRepository;

    @InjectMocks
    private ApiRequestService apiRequestService;

    @Test
    void testIsDuplicate() {
        UUID requestId = UUID.randomUUID();

        boolean isDuplicate = apiRequestService.isDuplicate(requestId);
        assertFalse(isDuplicate);

        isDuplicate = apiRequestService.isDuplicate(requestId);
        assertTrue(isDuplicate);
    }

    @Test
    void testProcessApiRequest() {
        LatestExchangeRequest request = new LatestExchangeRequest();
        request.setRequestId(UUID.randomUUID());
        request.setClient("1234");
        request.setCurrency("EUR");
        request.setTimestamp(System.currentTimeMillis());

        Currency currency = new Currency();
        currency.setSymbol("EUR");
        currency.setName("Euro");

        when(currencyRepository.findBySymbol(request.getCurrency())).thenReturn(Optional.of(currency));
        when(apiRequestRepository.save(any(ApiRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiRequest savedRequest = apiRequestService.processApiRequest(request);

        assertNotNull(savedRequest);
        assertEquals(request.getRequestId(), savedRequest.getId());
        assertEquals(request.getClient(), savedRequest.getEndUserID());
        assertEquals(currency, savedRequest.getCurrency());
        assertEquals(request.getTimestamp(), savedRequest.getTimestamp());

        verify(currencyRepository, times(1)).findBySymbol("EUR");
        verify(apiRequestRepository, times(1)).save(any(ApiRequest.class));
    }

    @Test
    void testProcessApiRequestCurrencyNotFound() {
        ExchangeApiRequest request = new LatestExchangeRequest();
        request.setRequestId(UUID.randomUUID());
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
