package com.xzydominic.xzymq.service.impl;

import com.xzydominic.xzymq.annotation.rabbitmq.ZyMessageProducer;
import com.xzydominic.xzymq.annotation.rabbitmq.ZyQueue;
import com.xzydominic.xzymq.entity.ExampleEntity;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RabbitServices {

    @Resource
    private RabbitTemplate rabbitTemplate;

    private String example;

    @Bean
    @ZyQueue(name = "my-queue")
    private void createQueue() {}

    @ZyMessageProducer(queueName = "my-queue", paramName = ExampleEntity.class, message = "")
    public void sendMessage(ExampleEntity exampleEntity) {}

    @ZyQueue(name = "my-queue")
    public void process(ExampleEntity exampleEntity) {
        this.sendMessage(exampleEntity);
    }

    public static void main(String[] args) {
        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setValue("dc");
        exampleEntity.setName("xx");
        RabbitServices rabbitServices = new RabbitServices();
        rabbitServices.process(exampleEntity);
    }
}
