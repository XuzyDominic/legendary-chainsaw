package com.xzydominic.xzymq.core.rabbitmq.ascept;

import com.xzydominic.xzymq.annotation.rabbitmq.ZyQueueBinding;
import jakarta.annotation.Resource;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class QueueBindingAspect {

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Resource
    private ApplicationContext applicationContext;

    @Before("@within(zyQueueBinding)")
    public void bindQueuesToExchange(ZyQueueBinding zyQueueBinding) {
        String exchangeName = zyQueueBinding.exchange();
        String routeKey = zyQueueBinding.routeKey();
        TopicExchange exchange = new TopicExchange(exchangeName);
        rabbitAdmin.declareExchange(exchange);

        String[] beanNames = applicationContext.getBeanNamesForAnnotation(zyQueueBinding.getClass());
        for (String beanName : beanNames) {
            if (beanName.equals("zyQueueBinding")) {
                continue;
            }
            Binding binding = null;
            if (rabbitAdmin.getQueueInfo(beanName) == null) {
                binding = BindingBuilder.bind(new Queue(beanName)).to(exchange).with(routeKey);
            } else {
                binding = BindingBuilder.bind(getQueueByName(beanName)).to(exchange).with(routeKey);
            }
            rabbitAdmin.declareBinding(binding);
        }
    }

    private Queue getQueueByName(String queueName) {
        return (Queue) Objects.requireNonNull(rabbitAdmin.getQueueProperties(queueName)).get("org.springframework.amqp.core.Queue");
    }

}
