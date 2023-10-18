package com.xzydominic.xzymq.core.rabbitmq.ascept;

import com.xzydominic.xzymq.annotation.rabbitmq.ZyQueue;
import jakarta.annotation.Resource;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicQueueAspect {

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.ZyQueue)")
    public void dynamicQueueMethods() {
    }

    @Before("dynamicQueueMethods() && @annotation(zyQueue)")
    public void createQueue(ZyQueue zyQueue) {
        String queueName = zyQueue.name();
        if (rabbitAdmin.getQueueInfo(queueName) != null) {
            return;
        }
        rabbitAdmin.declareQueue(new Queue(queueName, true));
    }
}
