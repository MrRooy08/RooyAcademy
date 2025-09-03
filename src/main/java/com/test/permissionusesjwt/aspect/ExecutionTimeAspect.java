package com.test.permissionusesjwt.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    // Log tất cả method trong controller
    @Around("execution(* com.test.permissionusesjwt.controller..*(..))")
    public Object logControllerTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint, "Controller");
    }

    // Log tất cả method trong service
    @Around("execution(* com.test.permissionusesjwt.service..*(..))")
    public Object logServiceTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint, "Service");
    }

    private Object logExecutionTime(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        log.info("⏱ [{}] {} xử lý mất {} ms", layer, joinPoint.getSignature(), duration);

        return result;
    }
}
