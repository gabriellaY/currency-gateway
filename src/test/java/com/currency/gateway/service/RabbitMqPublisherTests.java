package com.currency.gateway.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.currency.gateway.configuration.RabbitMqConfig;
import com.currency.gateway.entity.ApiRequest;
import com.currency.gateway.entity.Service;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RabbitMqPublisherTests {

    private final String TOPIC = "test.topic";
    private final String ROUTING_KEY = "test.routing-key";

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitMqConfig rabbitMqConfig;

    @InjectMocks
    private RabbitMqPublisher rabbitMqPublisher;

    private ApiRequest apiRequest;


    @BeforeEach
    public void setUp() {
        Service testService = new Service();
        testService.setName("test-service");
        apiRequest = new ApiRequest("12345", testService, "test-user", System.currentTimeMillis());

        when(rabbitMqConfig.getExchange()).thenReturn(TOPIC);
        when(rabbitMqConfig.getRoutingKey()).thenReturn(ROUTING_KEY);
    }

    @Test
    public void testPublishApiRequestSuccessful() {
        rabbitMqPublisher.publishApiRequest(apiRequest);

        verify(rabbitTemplate, times(1)).convertAndSend(TOPIC, ROUTING_KEY, apiRequest);
    }

    @Test
    public void testPublishApiRequestFailure() {
        doThrow(new RuntimeException("RabbitMQ is down")).when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(ApiRequest.class));

        assertDoesNotThrow(() -> rabbitMqPublisher.publishApiRequest(apiRequest));

        verify(rabbitTemplate, times(1)).convertAndSend(TOPIC, ROUTING_KEY, apiRequest);
    }
}
