package y_lab.audit_logging_spring_boot_starter.util;

public class DefaultUserContext implements UserContext{
    @Override
    public Long getUserId() {
        return -10L;
    }
}
