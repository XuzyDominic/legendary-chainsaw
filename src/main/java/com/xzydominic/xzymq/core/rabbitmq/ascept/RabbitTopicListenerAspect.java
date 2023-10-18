package com.xzydominic.xzymq.core.rabbitmq.ascept;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RabbitTopicListenerAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.ZyTopicListener)")
    public void rabbitTopicListenerPointcut() {
    }

    @AfterReturning(pointcut = "rabbitTopicListenerPointcut() && args(message)")
    public void receiveTopicMessage(String message) {
        // 执行消息接收逻辑，根据注解中的参数获取 Topic 交换机和路由键

    }

}
