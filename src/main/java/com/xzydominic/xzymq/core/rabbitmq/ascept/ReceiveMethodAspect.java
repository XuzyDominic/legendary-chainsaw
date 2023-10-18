package com.xzydominic.xzymq.core.rabbitmq.ascept;

import com.xzydominic.xzymq.annotation.rabbitmq.ZyReceiveMethod;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReceiveMethodAspect {

    @Pointcut("@annotation(com.xzydominic.xzymq.annotation.rabbitmq.ZyReceiveMethod)")
    public void receiveMethod() {
    }

    @Around("receiveMethod() && @annotation(zyReceiveMethod)")
    public Object executeReceiveMethod(ProceedingJoinPoint joinPoint, ZyReceiveMethod zyReceiveMethod) throws Throwable {
        String methodName = zyReceiveMethod.method();
        Object target = joinPoint.getTarget();
        target.getClass().getMethod(methodName).invoke(target);
        return joinPoint.proceed();
    }

}
