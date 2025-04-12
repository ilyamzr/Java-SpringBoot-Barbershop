package com.example.barbershop.log;

import java.util.List;
import java.util.Optional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.barbershop.controller.*.*(..))")
    public void logControllerBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Вход в метод контроллера: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.barbershop.controller.*.*(..))",
            returning = "result")
    public void logControllerAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("Выход из метода контроллера: {} с результатом: {}",
                    joinPoint.getSignature().toShortString(), result);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.example.barbershop.controller.*.*(..))",
            throwing = "error")
    public void logControllerError(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.error("Ошибка в методе контроллера: {} с причиной: {}",
                    joinPoint.getSignature().toShortString(), error.getMessage());
        }
    }

    @Before("execution(* com.example.barbershop.repository.*.findById(..))")
    public void logDbFindBefore(JoinPoint joinPoint) {
        Object id = joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromRepository(joinPoint
                .getTarget().getClass().getSimpleName());
        logger.info("Попытка получить {} с ID {} из базы данных", entityType, id);
    }

    @AfterReturning(pointcut = "execution(* com.example.barbershop.repository.*.findById(..))",
            returning = "result")
    public void logDbFindAfterReturning(JoinPoint joinPoint, Optional<?> result) {
        Object id = joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromRepository(joinPoint
                .getTarget().getClass().getSimpleName());
        if (result.isPresent()) {
            logger.info("{} с ID {} успешно получен из базы данных", entityType, id);
        } else {
            logger.info("{} с ID {} не найден в базе данных", entityType, id);
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.barbershop.repository.*.findAll())",
            returning = "result")
    public void logDbFindAllAfterReturning(JoinPoint joinPoint, List<?> result) {
        String entityType = getEntityTypeFromRepository(joinPoint
                .getTarget().getClass().getSimpleName());
        logger.info("Все {} успешно получены из базы данных, количество: {}",
                entityType, result.size());
    }

    @AfterThrowing(pointcut = "execution(* com.example.barbershop.service.*.*(..))",
            throwing = "error")
    public void logServiceError(JoinPoint joinPoint, Throwable error) {
        logger.error("Ошибка в сервисе: {} с причиной: {}",
                joinPoint.getSignature().getName(), error.getMessage());
    }

    private String getEntityTypeFromRepository(String repositoryName) {
        if (repositoryName.contains("Barber")) {
            return "Барбер";
        }
        if (repositoryName.contains("Location")) {
            return "Локация";
        }
        if (repositoryName.contains("Offering")) {
            return "Услуга";
        }
        return "Неизвестный тип";
    }
}