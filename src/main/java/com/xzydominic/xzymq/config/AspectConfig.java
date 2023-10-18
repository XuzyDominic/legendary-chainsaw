package com.xzydominic.xzymq.config;

import com.xzydominic.xzymq.core.rabbitmq.ascept.DynamicQueueAspect;
import com.xzydominic.xzymq.core.rabbitmq.ascept.QueueBindingAspect;
import com.xzydominic.xzymq.core.rabbitmq.ascept.RabbitProducerAspect;
import com.xzydominic.xzymq.core.rabbitmq.ascept.ReceiveMethodAspect;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AspectConfig {

    @Bean
    public RabbitAdmin rabbitAdmin() {
        ConnectionFactory connectionFactory = new ConnectionFactory() {
            @Override
            public Connection createConnection() throws AmqpException {
                return null;
            }

            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public int getPort() {
                return 5672;
            }

            @Override
            public String getVirtualHost() {
                return "/";
            }

            @Override
            public String getUsername() {
                return "admin";
            }

            @Override
            public void addConnectionListener(ConnectionListener connectionListener) {

            }

            @Override
            public boolean removeConnectionListener(ConnectionListener connectionListener) {
                return false;
            }

            @Override
            public void clearConnectionListeners() {

            }
        };
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public DynamicQueueAspect dynamicQueueAspect() {
        return new DynamicQueueAspect();
    }

    @Bean
    public RabbitProducerAspect rabbitProducerAspect() {
        return new RabbitProducerAspect();
    }

    @Bean
    public QueueBindingAspect zyQueueBindingAspect() {
        return new QueueBindingAspect();
    }

    @Bean
    public ReceiveMethodAspect receiveMethodAspect() {
        return new ReceiveMethodAspect();
    }

}
