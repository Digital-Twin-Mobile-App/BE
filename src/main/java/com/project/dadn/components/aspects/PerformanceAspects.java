package com.project.dadn.components.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class PerformanceAspects {

    @Pointcut("within(com.project.dadn.controllers.*)")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void beforeMethodExecution(JoinPoint joinPoint) {
        logMethodExecution(joinPoint, "Starting execution of");
    }

    @After("controllerMethods()")
    public void afterMethodExecution(JoinPoint joinPoint) {
        logMethodExecution(joinPoint, "Finished execution of");
    }

    @Around("controllerMethods()")
    public Object measureControllerMethodExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.nanoTime();

        Object returnValue = proceedingJoinPoint.proceed();

        long end = System.nanoTime();
        long durationInMs = TimeUnit.NANOSECONDS.toMillis(end - start);
        String methodName = proceedingJoinPoint.getSignature().getName();

        log.info("Execution of {} took {} ms", methodName, durationInMs);

        return returnValue;
    }

    private void logMethodExecution(JoinPoint joinPoint, String message) {
        String methodName = joinPoint.getSignature().getName();
        log.info("{} {}", message, methodName);
    }

}
