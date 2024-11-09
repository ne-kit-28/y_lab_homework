package y_lab.audit_logging_spring_boot_starter.repository;

/**
 * SqlScripts is stores sql scripts.
 */

public class SqlScripts {

    public static final String AUDIT_SAVE =
            "INSERT INTO service.audit (user_id, date, message) " +
                    "VALUES (?, ?, ?)";

    public static final String AUDIT_FIND_AUDIT_BY_USER_ID =
            "SELECT * FROM service.audit " +
                    "WHERE user_id = ?";
}
