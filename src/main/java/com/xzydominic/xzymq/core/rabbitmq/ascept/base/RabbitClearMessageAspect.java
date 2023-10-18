package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.Channel;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyClearMessage;
import com.xzydominic.xzymq.exception.encapsulation.CommonException;
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
public class RabbitClearMessageAspect {

    private static final Logger logger = LoggerFactory.getLogger(RabbitClearMessageAspect.class);

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyClearMessage)")
    public void rabbitClearMessageAspect() {}

    @AfterReturning("rabbitClearMessageAspect() && @annotation(zyClearMessage)")
    public void clearMessageFromQueue(ZyClearMessage zyClearMessage) throws IOException, TimeoutException {
        if (zyClearMessage.queueName().trim().equals("")) {
            throw new CommonException("queue name is empty");
        }
        Channel channel = GlobalRabbitMQConnectionFactory.getConnectionFactory().newConnection().createChannel();
        try {
            channel.queuePurge(zyClearMessage.queueName());
        } catch (Exception exception) {
            throw new IOException("clear queue '" + zyClearMessage.queueName() + "' failed");
        }
        logger.info("clear queue '" + zyClearMessage.queueName() + "' ok");
    }

}
