package y_lab.audit_logging_spring_boot_starter.annotation;

import org.springframework.context.annotation.Import;
import y_lab.audit_logging_spring_boot_starter.config.LoggingConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для включения логирования в проекте.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LoggingConfig.class)
public @interface EnableLogging {
}
