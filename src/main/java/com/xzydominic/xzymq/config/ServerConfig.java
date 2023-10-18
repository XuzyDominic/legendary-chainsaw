package com.xzydominic.xzymq.config;

import com.xzydominic.xzymq.utils.GlobalRabbitMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    public void setServer() {
        String[] server = {
                "http://127.0.0.1:5596/api/temp/test/1",
                "http://127.0.0.1:5596/api/temp/test/2"
        };
        GlobalRabbitMQConnectionFactory.setRemoteServiceURI(server);
    }

}
