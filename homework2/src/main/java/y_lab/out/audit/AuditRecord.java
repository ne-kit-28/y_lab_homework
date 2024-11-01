package y_lab.out.audit;

import java.time.LocalDateTime;

public record AuditRecord(
        Long userId,
        LocalDateTime date,
        String message
) {
}
