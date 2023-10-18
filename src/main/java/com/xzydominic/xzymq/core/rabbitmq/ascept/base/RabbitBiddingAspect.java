package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.Channel;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyBidding;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RabbitBiddingAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyBidding)")
    public void rabbitBiddingAspect() {}

    @Around("rabbitBiddingAspect() && @annotation(zyBidding)")
    public Object biddingQueueToExchange(ProceedingJoinPoint joinPoint, ZyBidding zyBidding) throws Throwable {
        if (zyBidding.exchangeName().trim().equals("") ||
            zyBidding.queueName().trim().equals("") ||
            zyBidding.routeKey().trim().equals("")) {
            throw new RuntimeException("bidding params error");
        }
        Channel channel = GlobalRabbitMQConnectionFactory.getConnectionFactory().newConnection().createChannel();
        try {
            channel.queueBind(zyBidding.queueName(), zyBidding.exchangeName(), zyBidding.routeKey());
        } catch (Exception exception) {
            throw new RuntimeException("bidding failed");
        }
        return joinPoint.proceed();
    }

}
