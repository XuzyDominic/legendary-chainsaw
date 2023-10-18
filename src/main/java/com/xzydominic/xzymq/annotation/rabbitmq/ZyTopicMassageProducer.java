package com.xzydominic.xzymq.annotation.rabbitmq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZyTopicMassageProducer {

    String exchange() default "";

    String routeKey() default "";

    Class<?> paramName() default Exception.class;

    String message() default "";

}
