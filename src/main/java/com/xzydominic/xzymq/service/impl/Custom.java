package com.xzydominic.xzymq.service.impl;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Custom {

    //监听队列
//    @RabbitListener(queues = "dominic")
//    public void listenMessage(Message message)
//    {
//        System.out.println("接收消息：" + message);
//    }
}
