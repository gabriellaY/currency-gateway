package com.currency.gateway.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.currency.gateway.configuration.RabbitMqConfig;
import com.currency.gateway.entity.ApiRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RabbitMqPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqConfig rabbitMqConfig;

    @Autowired
    public RabbitMqPublisher(RabbitTemplate rabbitTemplate, RabbitMqConfig rabbitMqConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMqConfig = rabbitMqConfig;
    }

    public void publishApiRequest(ApiRequest apiRequest) {
        try {
            rabbitTemplate.convertAndSend(rabbitMqConfig.getExchange(),
                                          rabbitMqConfig.getRoutingKey(),
                                          apiRequest);

            log.info("Message sent to exchange {} with routing key {}",
                     rabbitMqConfig.getExchange(), rabbitMqConfig.getRoutingKey());
        } catch (Exception e) {
            log.error("Failed to send message to exchange {} with routing key {}",
                      rabbitMqConfig.getExchange(), rabbitMqConfig.getRoutingKey(), e);
        }
    }

}
