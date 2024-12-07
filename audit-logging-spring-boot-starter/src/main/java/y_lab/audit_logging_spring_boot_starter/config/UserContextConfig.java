package y_lab.audit_logging_spring_boot_starter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import y_lab.audit_logging_spring_boot_starter.util.DefaultUserContext;
import y_lab.audit_logging_spring_boot_starter.util.UserContext;

/**
 * Конфигурация для получения userId из проекта, если в application.yml основного проекта нет
 * строки с env
 */
@Configuration
public class UserContextConfig {

    @Bean(name = "defaultUserContext")
    @ConditionalOnMissingBean(UserContext.class)
    public UserContext userContext() {
        return new DefaultUserContext();
    }
}