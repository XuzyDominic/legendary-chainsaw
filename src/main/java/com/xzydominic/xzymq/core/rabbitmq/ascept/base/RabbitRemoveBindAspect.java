package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.Channel;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRemoveBinding;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Aspect
@Component
public class RabbitRemoveBindAspect {

    private static final Logger logger = LoggerFactory.getLogger(RabbitRemoveBindAspect.class);

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRemoveBinding)")
    public void rabbitRemoveBindAspect() {}

    @After("rabbitRemoveBindAspect() && @annotation(zyRemoveBinding)")
    public void removeBinding(ZyRemoveBinding zyRemoveBinding) throws IOException {
        if (zyRemoveBinding.exchange().trim().equals("") ||
            zyRemoveBinding.queueName().trim().equals("")
        ) {
           throw new RuntimeException("params ignored");
        }
        try {
            Channel channel = GlobalRabbitMQConnectionFactory.getConnectionFactory().newConnection().createChannel();
            channel.queueUnbind(zyRemoveBinding.exchange(), zyRemoveBinding.queueName(), zyRemoveBinding.routeKey());
        } catch (Exception exception) {
            throw new IOException("unbind queue failed");
        }
        logger.info("unbind queue '" + zyRemoveBinding.queueName() + "'  from '" + zyRemoveBinding.exchange() + "' ok");
    }

}
