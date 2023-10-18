package com.xzydominic.xzymq.annotation.rabbitmq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZyFanoutMessageProducer {

    String exchange();

    Class<?> paramName() default Exception.class;

    String message() default "";

}
