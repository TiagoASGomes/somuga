package org.somuga.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* org.somuga.controller.*.*(..))")
    public void logRequest(JoinPoint joinPoint) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        String method = joinPoint.getSignature().toShortString();
        logger.info("User: {} is calling {}", user, method);
    }


}
