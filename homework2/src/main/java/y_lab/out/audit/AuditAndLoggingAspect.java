package y_lab.out.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AuditAndLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAndLoggingAspect.class);

    @Before("@annotation(auditAction)")
    public void auditAction(AuditAction auditAction) {
        String action = auditAction.action();

        logger.info("Пользователь совершил действие: {}", action);
    }

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        logger.info("Метод {} выполнен за {} мс", methodName, executionTime);

        return proceed;
    }
}
