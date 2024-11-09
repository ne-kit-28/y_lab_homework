package y_lab.audit_logging_spring_boot_starter.domain;

import java.time.LocalDateTime;

public record AuditRecord(
        Long userId,
        LocalDateTime date,
        String message
) {
}
