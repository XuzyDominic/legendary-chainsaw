package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyExchange;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Aspect
@Component
public class RabbitExchangeAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyExchange)")
    public void rabbitExchangeAspect() {}

    @Before("rabbitExchangeAspect() && @annotation(zyExchange)")
    public void createExchange(ZyExchange zyExchange) throws IOException, TimeoutException {
        if (zyExchange.exchangeName().trim().equals("") ||
            zyExchange.exchangeType().trim().equals("")) {
            throw new RuntimeException("exchange name or type is empty");
        }
        ConnectionFactory connectionFactory = GlobalRabbitMQConnectionFactory.getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        try {
            channel.exchangeDeclare(zyExchange.exchangeName(), zyExchange.exchangeType());
        } catch (IOException exception) {
            throw new IOException("create exchange failed");
        }
    }

}
