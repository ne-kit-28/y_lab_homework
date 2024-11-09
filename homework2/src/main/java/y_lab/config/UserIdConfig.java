package y_lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import y_lab.out.audit.UserContext;
import y_lab.out.audit.UserContextImpl;

@Configuration
public class UserIdConfig {
    @Bean
    public UserContext userContext() {
        return new UserContextImpl();
    }
}