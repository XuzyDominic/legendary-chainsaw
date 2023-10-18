package com.xzydominic.xzymq.controller;

import com.xzydominic.xzymq.core.rabbitmq.ascept.base.connect;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/send")

public class SendMessageController {

    @Resource
    private connect c;

    @GetMapping("/type/1")
    public String sendTypeOne() throws IOException, TimeoutException {
        c.send("haha", "");
        return "ok";
    }


    @GetMapping("/type/2")
    public String sendTypeTwo() throws IOException, TimeoutException {
        c.send2("666");
        return "ok";
    }

    @GetMapping("/type/3")
    public String sendTypeThree() {
        c.send3("xzy nb");
        return "ok";
    }

    @GetMapping("/create/queue")
    public String createQueue() {
        c.createQueue();
        return "ok";
    }

    @GetMapping("/create/exchange/1")
    public String createExchange() {
        c.createExchange();
        return "ok";
    }

    @GetMapping("/bind/1")
    public String bindOne() {
        c.bind();
        return "ok";
    }

    @GetMapping("/get/1")
    public String getMsg() {
        c.getMessage1();
        return "ok";
    }

}
