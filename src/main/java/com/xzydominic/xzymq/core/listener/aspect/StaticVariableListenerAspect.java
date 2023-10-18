package com.xzydominic.xzymq.core.listener.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StaticVariableListenerAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.listener.StaticVariableListener)")
    public void staticVariableListenerAspect() {}



}
