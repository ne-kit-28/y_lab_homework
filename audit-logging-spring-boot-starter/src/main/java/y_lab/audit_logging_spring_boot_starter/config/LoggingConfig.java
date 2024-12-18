package y_lab.audit_logging_spring_boot_starter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import y_lab.audit_logging_spring_boot_starter.aspect.LoggingAspect;
import y_lab.audit_logging_spring_boot_starter.util.LoggingMapProperties;

/**
 * Конфигурация для включения логирования в проекте.
 * Эта конфигурация подключается только если в проекте используется аннотация @EnableLogging.
 */
public class LoggingConfig {

    /**
     * Создает бин для аспекта логирования, если он не был определен вручную.
     *
     * @param properties - настройки для логирования
     * @return бин для аспекта логирования
     */
    @Bean
    @ConditionalOnMissingBean // Бин создается только если не существует бина такого типа
    public LoggingAspect loggingAspect(LoggingMapProperties properties) {
        return new LoggingAspect(properties);
    }
}
