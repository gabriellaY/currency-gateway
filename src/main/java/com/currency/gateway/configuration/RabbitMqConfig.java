package com.currency.gateway.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.template")
public class RabbitMqConfig {

    private String exchange;
    private String routingKey;
    private String receiveQueue;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue apiRequestsQueue() {
        return new Queue(receiveQueue);
    }

    @Bean
    public Binding apiRequestsBinding(Queue apiRequestsQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(apiRequestsQueue).to(topicExchange).with(routingKey);
    }
}
