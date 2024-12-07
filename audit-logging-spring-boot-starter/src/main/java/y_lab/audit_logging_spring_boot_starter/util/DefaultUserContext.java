package y_lab.audit_logging_spring_boot_starter.util;

/**
 * Дефолтная реализация-заглушка UserContext -а
 */

public class DefaultUserContext implements UserContext{
    @Override
    public Long getUserId() {
        return -10L;
    }
}
