package y_lab.audit_logging_spring_boot_starter.service;

import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;

import java.util.ArrayList;

/**
 * Интерфейс {@code AuditService} определяет контракт для работы с записями аудита.
 * Он предоставляет методы для создания записей аудита и получения записей
 * аудита для конкретного пользователя.
 */
public interface AuditService {

    /**
     * Создает новую запись аудита.
     *
     * @param auditRecord объект {@link AuditRecord}, который нужно сохранить.
     */
    void createAudit(AuditRecord auditRecord);

    /**
     * Получает список записей аудита для указанного пользователя.
     *
     * @param userId идентификатор пользователя, для которого нужно получить записи аудита.
     * @return список записей аудита, связанных с указанным пользователем.
     */
    ArrayList<AuditRecord> getAudit(long userId);
}
