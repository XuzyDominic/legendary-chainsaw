package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyMessageCount;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Aspect
@Component
public class MassageCountAspect {

    private static final Logger logger = LoggerFactory.getLogger(MassageCountAspect.class);

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyMessageCount)")
    public void messageCountAspect() {}

    @AfterReturning("messageCountAspect() && @annotation(zyMessageCount)")
    public void logMessageCount(ZyMessageCount zyMessageCount) {
        if (zyMessageCount.queueName().trim().equals("")) {
            throw new RuntimeException("queue name is empty");
        }
        try {
            Channel channel = GlobalRabbitMQConnectionFactory.getConnectionFactory().newConnection().createChannel();
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(zyMessageCount.queueName());
            logger.info("The queue '" + zyMessageCount.queueName() + "' has " + declareOk.getMessageCount() + " messages");
            System.out.println("The queue '" + zyMessageCount.queueName() + "' has " + declareOk.getMessageCount() + " messages");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
