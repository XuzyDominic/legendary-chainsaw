package com.xzydominic.xzymq.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitBaseConfig {

    @Bean
    public Queue queue() {
        return new Queue("dominic");
    }

    @Bean
    public Exchange getExchange() {
        return ExchangeBuilder
                .topicExchange("xzy")
                .durable(true)
                .build();
    }

    @Bean
    public RestTemplate template() {
        return new RestTemplate();
    }

}
