package com.library.app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    @Around("serviceLayer() || controllerLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            logger.info("Exiting method: {} with result: {} and time taken: {} ms", joinPoint.getSignature(), result, elapsedTime);
            return result;
        } catch (Exception e) {
            logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), e.getCause() != null ? e.getCause() : "NULL");
            throw e;
        }
    }

    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), e.getCause() != null ? e.getCause() : "NULL");
    }
}
