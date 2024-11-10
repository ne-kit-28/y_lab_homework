package y_lab.audit_logging_spring_boot_starter.aspect;

import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;
import y_lab.audit_logging_spring_boot_starter.service.AuditService;
import y_lab.audit_logging_spring_boot_starter.util.UserContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import y_lab.audit_logging_spring_boot_starter.service.AuditServiceImpl;

import java.time.LocalDateTime;

/**
 * Аспект {@code AuditAspect} отвечает за аудит
 * действий пользователя.
 * <p>
 * Этот аспект перехватывает вызовы методов и создает записи аудита
 * с указанием идентификатора пользователя, времени выполнения и имени метода.
 */
@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;
    private final UserContext userContext;

    /**
     * Создает экземпляр {@code AuditAndLoggingAspect} с указанным сервисом аудита.
     *
     * @param auditService сервис для обработки записей аудита.
     */
    @Autowired
    public AuditAspect(@Lazy AuditServiceImpl auditService, UserContext userContext) {
        this.auditService = auditService;
        this.userContext = userContext;
    }

    /**
     * Определяет точку соединения для выполнения методов в сервисном слое.
     */
    @Pointcut("@annotation(y_lab.audit_logging_spring_boot_starter.annotation.Auditable)")
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
}
