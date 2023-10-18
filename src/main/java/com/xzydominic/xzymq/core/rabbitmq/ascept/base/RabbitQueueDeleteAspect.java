package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyDeleteQueue;
import com.xzydominic.xzymq.exception.encapsulation.QueueException;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Aspect
@Component
public class RabbitQueueDeleteAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyDeleteQueue)")
    public void rabbitQueueDeleteAspect() {}

    @AfterReturning("rabbitQueueDeleteAspect() && @annotation(zyDeleteQueue)")
    public void executeDeleteQueue(ZyDeleteQueue zyDeleteQueue) throws QueueException, IOException, TimeoutException {
        if (zyDeleteQueue.queueName().trim().equals("")) {
            throw new QueueException("delete queue failed");
        }
        Channel channel = GlobalRabbitMQConnectionFactory.getConnectionFactory().newConnection().createChannel();
        AMQP.Queue.DeleteOk deleteOk = null;
        try {
            deleteOk = channel.queueDelete(zyDeleteQueue.queueName());
        } catch (Exception exception) {
            throw new QueueException(exception.getMessage());
        }
        if (deleteOk == null) {
            throw new QueueException("No such queue in RabbitMQ client");
        }
    }
}
