package com.xzydominic.xzymq.core.rabbitmq.ascept;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xzydominic.xzymq.annotation.rabbitmq.ZyTopicMassageProducer;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import com.xzydominic.xzymq.utils.ParamsUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TopicMassageProducerAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.ZyTopicMassageProducer)")
    public void topicMassageProducerAspect() {
    }

    @Around("topicMassageProducerAspect() && @annotation(zyTopicMassageProducer)")
    public Object sendToTopicExchange(ProceedingJoinPoint joinPoint, ZyTopicMassageProducer zyTopicMassageProducer) throws Throwable {
        ConnectionFactory connectionFactory = GlobalRabbitMQConnectionFactory.getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        if (zyTopicMassageProducer.message().trim().equals("")) {
            Object var2 = ParamsUtils.getParamByMethod(joinPoint.getArgs(),
                    zyTopicMassageProducer.paramName());
            channel.basicPublish(zyTopicMassageProducer.exchange(),
                    zyTopicMassageProducer.routeKey(), null,
                    var2.toString().getBytes());
        } else {
            channel.basicPublish(zyTopicMassageProducer.exchange(),
                    zyTopicMassageProducer.routeKey(), null,
                    zyTopicMassageProducer.message().getBytes());
        }
        return joinPoint.proceed();
    }

}
