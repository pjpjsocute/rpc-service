package org.example.ray.circuitbreaker.annotation;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.ray.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhoulei
 * @create 2024/6/5
 * @description:
 */

@Aspect
@Component
@Slf4j
public class CircuitBreakerAspect {

    private static final int                                DEFAULT_SUCCESS_THRESHOLD = 5;

    private static final int                                DEFAULT_FAIL_THRESHOLD    = 5;

    private static final Duration                           DEFAULT_TIMEOUT           = Duration.ofSeconds(5);

    private final ConcurrentHashMap<String, CircuitBreaker> circuitBreakers           = new ConcurrentHashMap<>();

    @Pointcut("@annotation(org.example.ray.circuitbreaker.annotation.CircuitBreakerEnable)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        CircuitBreakerEnable annotation = getAnnotation(joinPoint);
        String circuitName = getCircuitNameOfDefault(annotation, joinPoint);
        int successThreshold =
            annotation.successThreshold() == 0 ? DEFAULT_SUCCESS_THRESHOLD : annotation.successThreshold();
        int failureThreshold =
            annotation.failureThreshold() == 0 ? DEFAULT_FAIL_THRESHOLD : annotation.failureThreshold();
        Duration duration =
            annotation.failureDuration() == 0 ? DEFAULT_TIMEOUT : Duration.ofSeconds(annotation.failureDuration());

        CircuitBreaker circuitBreaker = circuitBreakers.computeIfAbsent(circuitName,
            v -> new CircuitBreaker(failureThreshold, successThreshold, duration));
        if (!circuitBreaker.requestPermission()) {
            throw new RuntimeException("CircuitBreaker is open for: " + circuitName);
        }

        try {
            Object result = joinPoint.proceed();
            circuitBreaker.successRequest();
            return result;
        } catch (Throwable throwable) {
            circuitBreaker.failureRequest();
            throw throwable;
        }
    }


    private CircuitBreakerEnable getAnnotation(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        CircuitBreakerEnable annotation = method.getAnnotation(CircuitBreakerEnable.class);
        return annotation;
    }

    private String getCircuitNameOfDefault(CircuitBreakerEnable annotation, ProceedingJoinPoint joinPoint) {
        if (StringUtils.isNotEmpty(annotation.name())) {
            return annotation.name();
        }
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String targetName = className + "." + methodName;
        return targetName;
    }

}
