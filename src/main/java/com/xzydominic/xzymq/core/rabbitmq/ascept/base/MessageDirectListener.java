package com.xzydominic.xzymq.core.rabbitmq.ascept.base;

import com.rabbitmq.client.*;
import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyMessageListener;
import com.xzydominic.xzymq.core.manager.StaticVariableManager;
import com.xzydominic.xzymq.exception.encapsulation.CommonException;
import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

@Aspect
@Component
@SuppressWarnings("all")
public class MessageDirectListener {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MessageDirectListener.class);

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.base.ZyMessageListener)")
    public void messageDirectListener() {
    }

    @Around("messageDirectListener() && @annotation(zyMessageListener)")
    public Object listenMessage(ProceedingJoinPoint joinPoint, ZyMessageListener zyMessageListener) throws Throwable {
        if (zyMessageListener.queueName().trim().equals("")) {
            throw new CommonException("Queue name is empty");
        }
        String queueName = zyMessageListener.queueName();
        ConnectionFactory connectionFactory = GlobalRabbitMQConnectionFactory.getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        if (channel.queueDeclarePassive(queueName) == null) {
            throw new IOException("Queue not exist");
        }
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received: " + message);
            if (zyMessageListener.exclusive()) {
                StaticVariableManager.removeAllListener();
            }
            StaticVariableManager.setStaticVariable(message);
            try {
                joinPoint.proceed(new Object[]{message});
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
        channel.basicConsume(zyMessageListener.queueName(), false, "", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                String var1 = new String(body, StandardCharsets.UTF_8);
                String methodName = zyMessageListener.method();
                if (zyMessageListener.notParam()) {
                    notifyWithNoParams(zyMessageListener, joinPoint, var1);
                } else {
                    stringRedisTemplate.opsForValue().set(queueName, var1, 60000);
                    String[] remoteServiceURI = GlobalRabbitMQConnectionFactory.getRemoteServiceURI();
                    int efficientURI = GlobalRabbitMQConnectionFactory.getEfficientURI();
                    for (int var2 = 0; var2 < efficientURI; ) {
                        restTemplate.getForEntity(setQuestionMarks(remoteServiceURI[var2]) + deliveryTag, String.class);
                        var2 ++;
                    }
                    logger.info("Successfully called other servers");
                }
                channel.basicAck(deliveryTag, false);
                logger.info("Rabbit queue message received --- from '" + queueName + "'");
            }
        });
        return joinPoint.proceed();
    }

    private void notifyWithNoParams(ZyMessageListener zyMessageListener, ProceedingJoinPoint joinPoint, String str) throws IOException {
        if (!zyMessageListener.method().trim().equals("")) {
            Object target = joinPoint.getTarget();
            try {
                target.getClass().getMethod(zyMessageListener.method()).invoke(target);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IOException("Received message failed: Not found the method '" + zyMessageListener.method() + "' on this class");
            }
        } else {
            logger.warn("Received message: '" + str + "', but no processing method specified");
        }
    }

    private static String setQuestionMarks(String URI) {
        int count = 0;
        for (int i = 0; i < URI.length(); i++) {
            if (URI.charAt(i) == '?') {
                count ++;
            }
            if (count >= 2) {
                return URI + "&&tag=";
            }
        }
        return URI + "?tag=";
    }
}
