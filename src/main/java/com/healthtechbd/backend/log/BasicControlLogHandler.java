package com.healthtechbd.backend.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class BasicControlLogHandler {
    private final Logger logger = LoggerFactory.getLogger(BasicControlLogHandler.class);
    long startTime;

    @Before("execution(* com.healthtechbd.backend.controller..*(..))")
    public void before(JoinPoint joinPoint) {
        logger.info("Starting - " + joinPoint.getSignature());
        startTime = System.currentTimeMillis();
    }

    @AfterReturning("execution(* com.healthtechbd.backend.controller..*(..))")
    public void afterReturn(JoinPoint joinPoint) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        logger.info(joinPoint.getSignature() + " executed in " + executionTime + " milliseconds.");
    }


    @After("execution(* com.healthtechbd.backend.controller..*(..))")
    public void after(JoinPoint joinPoint) {
        logger.info("Finished - " + joinPoint.getSignature());
    }

    @AfterThrowing(value = "execution(* com.healthtechbd.backend.controller..*(..))", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Exception in " + joinPoint.getSignature(), ex);
    }
}
