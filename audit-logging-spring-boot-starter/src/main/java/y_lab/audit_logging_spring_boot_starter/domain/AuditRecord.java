package y_lab.audit_logging_spring_boot_starter.domain;

import java.time.LocalDateTime;

/**
 * Represents an audit record containing information about a specific event.
 *
 * <p>This record is used for storing details about an audit event, such as
 * the user who triggered it, the timestamp, and a descriptive message.</p>
 *
 * @param userId  the ID of the user associated with this audit event
 * @param date    the timestamp of the event occurrence
 * @param message a description of the audit event
 */

public record AuditRecord(
        Long userId,
        LocalDateTime date,
        String message
) {
}
