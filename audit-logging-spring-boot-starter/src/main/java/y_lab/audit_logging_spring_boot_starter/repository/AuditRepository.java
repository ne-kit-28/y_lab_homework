package y_lab.audit_logging_spring_boot_starter.repository;

import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Интерфейс {@code AuditRepository} определяет контракт для работы с записями аудита.
 * Он предоставляет методы для сохранения записей аудита и получения записей
 * аудита для конкретного пользователя из базы данных.
 */
public interface AuditRepository {

    /**
     * Сохраняет запись аудита в базе данных.
     *
     * @param auditRecord объект {@link AuditRecord}, который нужно сохранить.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    void save(AuditRecord auditRecord) throws SQLException;

    /**
     * Получает список записей аудита для указанного пользователя из базы данных.
     *
     * @param userId идентификатор пользователя, для которого нужно получить записи аудита.
     * @return список записей аудита, связанных с указанным пользователем.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    ArrayList<AuditRecord> getAuditByUserId(long userId) throws SQLException;
}
