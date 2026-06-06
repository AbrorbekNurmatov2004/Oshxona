package oshxona.oshxona.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* oshxona.oshxona.service.*.*(..))")
    public void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info(">>> Method start: {} | Args: {}", method, Arrays.asList(args));
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("<<< Method end: {} | Result: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* oshxona.oshxona.validator.*.*(..))", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Exception e) {
        log.error("<<< Validation error: {} | Reason: {}", joinPoint.getSignature().getName(), e.getMessage());
    }

    @Around("serviceLayer()")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long Spent = System.currentTimeMillis() - start;

            if (Spent > 1000) {
                log.warn("Method: {} working to slow - {}ms",
                        joinPoint.getSignature().getName(), Spent
                );
            }
            return result;
        } catch (Exception e) {
            long Spent = System.currentTimeMillis();
            log.error("Exception: {} - {}ms",
                    joinPoint.getSignature().getName(), Spent
            );
            throw e;
        }
    }
}
