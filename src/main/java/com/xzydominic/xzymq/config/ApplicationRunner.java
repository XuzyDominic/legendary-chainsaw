package com.xzydominic.xzymq.config;

import com.xzydominic.xzymq.annotation.rabbitmq.base.ZyRabbitConnect;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
public class ApplicationRunner implements CommandLineRunner {

    @Override
    @ZyRabbitConnect(username = "admin", password = "xzy163")
    public void run(String... args) throws Exception {}

}
