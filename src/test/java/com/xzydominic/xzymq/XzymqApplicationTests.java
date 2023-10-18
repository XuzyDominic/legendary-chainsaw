package com.xzydominic.xzymq;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = XzymqApplication.class)
class XzymqApplicationTests {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

//    @Test
//    public void testSendMessage() {
//        rabbitTemplate.convertAndSend("xzy","message","xzy666");
//    }

}
