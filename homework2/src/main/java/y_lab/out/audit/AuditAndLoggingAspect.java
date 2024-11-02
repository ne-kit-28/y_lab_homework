package y_lab.out.audit;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Aspect for auditing and logging method execution details.
 */
@Aspect
@Component
public class AuditAndLoggingAspect {

    private final AuditService auditService;

    @Autowired
    public AuditAndLoggingAspect(@Lazy AuditServiceImpl auditService) {
        this.auditService = auditService;
    }

    @Pointcut("execution(public * y_lab.service.serviceImpl.*.*(..))")
    public void serviceLayerExecution() {}

    @Before("serviceLayerExecution()")
    public void logAudit(JoinPoint joinPoint) {
        Long userId = UserContext.getUserId();
        if (userId == null)
            userId = -1L;
        String methodName = joinPoint.getSignature().getName();

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), methodName);
        auditService.createAudit(record);
    }

    @Around("serviceLayerExecution()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        Long userId = UserContext.getUserId();
        if (userId == null)
            userId = -1L;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        String message = "Метод " + methodName + " выполнен за " + executionTime;

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), message);
        auditService.createAudit(record);

        return proceed;
    }

    @AfterReturning(pointcut = "serviceLayerExecution()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {

        Long userId = UserContext.getUserId();
        if (userId == null)
            userId = -1L;
        String message = "Метод завершен: " + joinPoint.getSignature().getName()
                + ", возвращено значение: " + result;

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), message);
        auditService.createAudit(record);
    }

    @AfterThrowing(pointcut = "serviceLayerExecution()", throwing = "exception")
    public void logException(Throwable exception) {
        Long userId = UserContext.getUserId();
        if (userId == null)
            userId = -1L;
        String message = "Исключение " + exception.getMessage();

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), message);
        auditService.createAudit(record);
    }
}
