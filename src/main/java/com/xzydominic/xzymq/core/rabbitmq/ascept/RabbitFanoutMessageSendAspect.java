package com.xzydominic.xzymq.core.rabbitmq.ascept;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.xzydominic.xzymq.annotation.rabbitmq.ZyFanoutMessageProducer;
import com.xzydominic.xzymq.exception.encapsulation.CommonException;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import com.xzydominic.xzymq.utils.ParamsUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RabbitFanoutMessageSendAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.ZyFanoutMessageProducer)")
    public void rabbitFanoutMessageSendAspect() {}

    @Around("rabbitFanoutMessageSendAspect() && @annotation(zyFanoutMessageProducer)")
    public Object sendFanoutMessage(ProceedingJoinPoint joinPoint, ZyFanoutMessageProducer zyFanoutMessageProducer) throws Throwable {
        if (zyFanoutMessageProducer.exchange().trim().equals("")) {
            throw new CommonException("exchange name is empty");
        }
        Channel channel = GlobalRabbitMQConnectionFactory.getConnectionFactory().newConnection().createChannel();
//        try {
//            AMQP.Exchange.DeclareOk declareOk = channel.exchangeDeclarePassive(zyFanoutMessageProducer.exchange());
//
//        }
        if (!zyFanoutMessageProducer.message().trim().equals("")) {
            channel.basicPublish(zyFanoutMessageProducer.exchange(), "", null, zyFanoutMessageProducer.message().getBytes());
        } else {
            Object messageObject = ParamsUtils.getParamByMethod(joinPoint.getArgs(), zyFanoutMessageProducer.paramName());
            channel.basicPublish(zyFanoutMessageProducer.exchange(), "", null, messageObject.toString().getBytes());
        }
        return joinPoint.proceed();
    }

}
