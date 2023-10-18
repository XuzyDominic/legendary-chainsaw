package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyQueue;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Aspect
@Component
public class RabbitQueueAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyQueue)")
    public void rabbitQueueAspect() {}

    @Before("rabbitQueueAspect() && @annotation(zyQueue)")
    public void createQueue(ZyQueue zyQueue) throws Throwable {
        if (zyQueue.queueName().trim().equals("")) {
            throw new RuntimeException("queue name is empty");
        }
        try {
            ConnectionFactory connectionFactory = GlobalRabbitMQConnectionFactory.getConnectionFactory();
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(zyQueue.queueName(), zyQueue.durable(), zyQueue.exclusive(),
                    zyQueue.autoDelete(), null);
        } catch (IOException exception) {
            throw new RuntimeException("create queue error -> " + exception.getMessage());
        }
    }
}
