package y_lab.audit_logging_spring_boot_starter.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;
import y_lab.audit_logging_spring_boot_starter.service.AuditService;
import y_lab.audit_logging_spring_boot_starter.service.AuditServiceImpl;
import y_lab.audit_logging_spring_boot_starter.util.UserContext;

import java.time.LocalDateTime;

/**
 * Аспект {@code AuditAndLoggingAspect} отвечает за аудит и логирование
 * деталей выполнения методов в сервисном слое приложения.
 *
 * Этот аспект перехватывает вызовы методов в сервисном слое и создает записи аудита
 * с указанием идентификатора пользователя, времени выполнения и имени метода.
 */
@Aspect
@Component
public class AuditAndLoggingAspect {

    private final AuditService auditService;
    private final UserContext userContext;

    /**
     * Создает экземпляр {@code AuditAndLoggingAspect} с указанным сервисом аудита.
     *
     * @param auditService сервис для обработки записей аудита.
     */
    @Autowired
    public AuditAndLoggingAspect(@Lazy AuditServiceImpl auditService, UserContext userContext) {
        this.auditService = auditService;
        this.userContext = userContext;
    }

    /**
     * Определяет точку соединения для выполнения методов в сервисном слое.
     */
    @Pointcut("execution(public * y_lab.service.serviceImpl.*.*(..))")
    public void serviceLayerExecution() {}

    /**
     * Логирует начало выполнения метода и создает запись аудита до его вызова.
     *
     * @param joinPoint информация о вызванном методе.
     */
    @Before("serviceLayerExecution()")
    public void logAudit(JoinPoint joinPoint) {
        Long userId = userContext.getUserId();
        if (userId == null)
            userId = -1L;
        String methodName = joinPoint.getSignature().getName();

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), methodName);
        auditService.createAudit(record);
    }

    /**
     * Логирует время выполнения метода и создает запись аудита после его завершения.
     *
     * @param joinPoint информация о вызванном методе.
     * @return результат выполнения метода.
     * @throws Throwable если при выполнении метода возникает исключение.
     */
    @Around("serviceLayerExecution()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        Long userId = userContext.getUserId();
        if (userId == null)
            userId = -1L;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        String message = "Метод " + methodName + " выполнен за " + executionTime;

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), message);
        auditService.createAudit(record);

        return proceed;
    }

    /**
     * Логирует завершение метода и создает запись аудита с возвращаемым значением.
     *
     * @param joinPoint информация о вызванном методе.
     * @param result возвращаемое значение метода.
     */
    @AfterReturning(pointcut = "serviceLayerExecution()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {

        Long userId = userContext.getUserId();
        if (userId == null)
            userId = -1L;
        String message = "Метод завершен: " + joinPoint.getSignature().getName()
                + ", возвращено значение: " + result;

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), message);
        auditService.createAudit(record);
    }

    /**
     * Логирует исключения, возникающие во время выполнения метода.
     *
     * @param exception исключение, возникшее при выполнении метода.
     */
    @AfterThrowing(pointcut = "serviceLayerExecution()", throwing = "exception")
    public void logException(Throwable exception) {
        Long userId = userContext.getUserId();
        if (userId == null)
            userId = -1L;
        String message = "Исключение " + exception.getMessage();

        AuditRecord record = new AuditRecord(userId, LocalDateTime.now(), message);
        auditService.createAudit(record);
    }
}
