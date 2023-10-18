package com.xzydominic.xzymq.core.rabbitmq.ascept;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xzydominic.xzymq.annotation.rabbitmq.ZyMessageProducer;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import com.xzydominic.xzymq.utils.ParamsUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Objects;

@Aspect
public class RabbitProducerAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.ZyMessageProducer)")
    public void rabbitProducerMethods() {}

    @Around("rabbitProducerMethods() && @annotation(zyMessageProducer)")
    public Object sendMessage(ProceedingJoinPoint joinPoint, ZyMessageProducer zyMessageProducer) throws Throwable {
        ConnectionFactory connectionFactory = GlobalRabbitMQConnectionFactory.getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = zyMessageProducer.queueName();
        String message = zyMessageProducer.message();
        Class<?> clazz = zyMessageProducer.paramName();
        Object realObject = null;
        Object[] args = joinPoint.getArgs();
        if (channel.queueDeclarePassive(queueName) == null) {
            channel.queueDeclare(queueName, false, false, false, null);
        }
        if (Objects.equals(message.trim(), "")) {
            realObject = ParamsUtils.getParamByMethod(args, clazz);
            channel.basicPublish("", queueName, null, realObject.toString().getBytes());
        } else {
            channel.basicPublish("", queueName, null, message.getBytes());
        }
        return joinPoint.proceed();
    }

}
