package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xzydominic.xzymq.access.StaticVariableListener;
import com.xzydominic.xzymq.annotation.rabbitmq.ZyTopicMassageProducer;
import com.xzydominic.xzymq.annotation.rabbitmq.base.*;
import com.xzydominic.xzymq.constant.ExchangeType;
import com.xzydominic.xzymq.annotation.rabbitmq.ZyMessageProducer;
import com.xzydominic.xzymq.core.manager.StaticVariableManager;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Service
public class connect {

    public void send(String message, String routingKey) throws IOException, TimeoutException {
        message = (new Date()) + ": " + message;
        ConnectionFactory factory = GlobalRabbitMQConnectionFactory.getConnectionFactory();
        System.out.println(factory.getPassword());
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish("", "xzyQueue2", null, message.getBytes());
        System.out.println("send ok");
    }

    @ZyMessageProducer(queueName = "xzyQueue2", paramName = String.class)
    @ZyMessageCount(queueName = "xzyQueue2")
    public void send2(String message) {
        message = (new Date()) + ": " + message;
    }

    @ZyTopicMassageProducer(exchange = "xzyExchange", routeKey = "topic.1", paramName = String.class, message = " ")
    @ZyMessageCount(queueName = "xzyQueue3")
    public void send3(String message) {

    }

    @ZyQueue(queueName = "xzyQueue3")
    public void createQueue() {}

    @ZyExchange(exchangeName = "xzyExchange", exchangeType = ExchangeType.TOPIC)
    public void createExchange() {}

    @ZyMessageCount(queueName = "xzyQueue2")
    @ZyBidding(queueName = "xzyQueue1", exchangeName = "xzyExchange", routeKey = "topic.2")
    public void bind() {}

    @ZyMessageListener(queueName = "xzyQueue3", notParam = false, exclusive = true)
    public void getMessage1() {}

}
