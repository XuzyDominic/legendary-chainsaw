package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.ConnectionFactory;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRabbitConnect;
import com.xzydominic.xzymq.exception.encapsulation.ConnectException;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RabbitConnectAspect {

    private static final Logger logger = LoggerFactory.getLogger(RabbitConnectAspect.class);

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRabbitConnect)")
    public void rabbitConnectAspect() {}

    @Before("rabbitConnectAspect() && @annotation(zyRabbitConnect)")
    public void createConnect(ZyRabbitConnect zyRabbitConnect) throws ConnectException {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setPassword(zyRabbitConnect.password());
            factory.setHost(zyRabbitConnect.host());
            factory.setPort(zyRabbitConnect.port());
            factory.setUsername(zyRabbitConnect.username());
            factory.setPassword(zyRabbitConnect.password());
            factory.setNetworkRecoveryInterval(zyRabbitConnect.networkInterval());
            factory.setTopologyRecoveryEnabled(zyRabbitConnect.topologyEnabled());
            GlobalRabbitMQConnectionFactory.setConnectionFactory(factory);
        } catch (Exception exception) {
            logger.error("RabbitMQ(ZY) set ConnectionFactory failed");
            throw new ConnectException("RabbitMQ(ZY) set ConnectionFactory failed");
        }
        logger.info("ZyRabbit framework is running...");
    }

}
