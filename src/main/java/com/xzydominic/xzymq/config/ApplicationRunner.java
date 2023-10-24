package com.xzydominic.xzymq.config;

import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRabbitConnect;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
public class ApplicationRunner implements CommandLineRunner {

    @Override
    @ZyRabbitConnect(host = "124.222.89.79", username = "admin", password = "1234567")
    public void run(String... args) throws Exception {}

}
