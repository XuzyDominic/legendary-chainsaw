package com.xzydominic.xzymq.annotation.rabbitmq.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZyRabbitConnect {

    String host() default "localhost";

    int port() default 5672;

    String virtualHost() default "/";

    String username() default "guest";

    String password() default "guest";

    int networkInterval() default 5000;

    boolean topologyEnabled() default true;

}
