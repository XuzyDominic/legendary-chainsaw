package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.*;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRemoveExchange;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Aspect
@Component
public class RemoveExchangeAspect {

    private static final Logger logger = LoggerFactory.getLogger(RabbitRemoveBindAspect.class);

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRemoveExchange)")
    public void removeExchangeAspect() {}

    @After("removeExchangeAspect() && @annotation(zyRemoveExchange)")
    public void removeExchange(ZyRemoveExchange zyRemoveExchange) throws IOException, TimeoutException {
        Optional.ofNullable(zyRemoveExchange.exchangeName())
                .orElseThrow(() -> new RuntimeException("exchange name is null"));
        ConnectionFactory connectionFactory = GlobalRabbitMQConnectionFactory.getConnectionFactory();
        Channel channel = connectionFactory.newConnection().createChannel();
        String[] exchangeNames = zyRemoveExchange.exchangeName();
        for (String exchangeName : exchangeNames) {
            try {
                channel.exchangeDelete(exchangeName);
            } catch (ShutdownSignalException shutdownSignalException) {
                throw new IOException("delete exchange '" + exchangeName + "' failed" +
                        ", process exited");
            }
        }
        logger.info("remove exchanges ok, those exchanges are" + Arrays.toString(exchangeNames));
    }

}
