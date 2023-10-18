package com.xzydominic.xzymq.annotation.rabbitmq.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZyQueue {

    String queueName();

    boolean durable() default true;

    boolean exclusive() default false;

    boolean autoDelete() default false;

}
