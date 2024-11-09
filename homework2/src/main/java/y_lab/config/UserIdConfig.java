package y_lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import y_lab.audit_logging_spring_boot_starter.util.UserContext;
import y_lab.out.audit.UserContextImpl;

@Configuration
public class UserIdConfig {
    @Bean
    @Primary
    public UserContext userContext() {
        return new UserContextImpl();
    }
}