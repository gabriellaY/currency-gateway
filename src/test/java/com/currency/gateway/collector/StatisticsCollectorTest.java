package com.currency.gateway.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.repository.ApiRequestRepository;

@SpringBootTest
public class StatisticsCollectorTest {
    @Mock
    private ApiRequestRepository apiRequestRepository;

    @InjectMocks
    private StatisticsCollector statisticsCollector;
    
    @Test
    void testSaveApiRequestStatistics() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setId("123");
        apiRequest.setEndUserID("TestClient");
        apiRequest.setTimestamp(System.currentTimeMillis());

        ApiRequest savedApiRequest = new ApiRequest();
        savedApiRequest.setId("123");
        savedApiRequest.setEndUserID("TestClient");
        savedApiRequest.setTimestamp(System.currentTimeMillis());

        when(apiRequestRepository.save(any(ApiRequest.class))).thenReturn(savedApiRequest);

        ApiRequest result = statisticsCollector.saveApiRequestStatistics(apiRequest);

        assertNotNull(result);
        assertEquals(savedApiRequest, result);
        verify(apiRequestRepository, times(1)).save(apiRequest);
    }
}
