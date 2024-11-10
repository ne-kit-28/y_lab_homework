package y_lab.audit_logging_spring_boot_starter.util;

import org.springframework.context.annotation.Import;
import y_lab.audit_logging_spring_boot_starter.config.LoggingConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для включения логирования в проекте.
 */
@Target(ElementType.TYPE) // Можно использовать на уровне классов
@Retention(RetentionPolicy.RUNTIME) // Доступна во время выполнения
@Import(LoggingConfig.class) // Подключение конфигурации стартеров
public @interface EnableLogging {
}
