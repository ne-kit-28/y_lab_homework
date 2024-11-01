package y_lab.out.audit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to indicate an audit action that should be logged.
 * <p>
 * This annotation can be applied to methods to specify the action being audited.
 * The action description can be retrieved and logged during the method execution.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditAction {
    /**
     * The description of the action being audited.
     *
     * @return the action description
     */
    String action() default "";
}
