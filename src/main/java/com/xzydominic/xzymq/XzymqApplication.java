package com.xzydominic.xzymq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class XzymqApplication {

    public static void main(String[] args) {
        SpringApplication.run(XzymqApplication.class, args);
    }

}
