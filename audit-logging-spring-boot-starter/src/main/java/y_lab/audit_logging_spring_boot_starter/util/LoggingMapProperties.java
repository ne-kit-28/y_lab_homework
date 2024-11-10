package y_lab.audit_logging_spring_boot_starter.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@Primary
@ConfigurationProperties(prefix = "log-package-starter")
public class LoggingMapProperties {
    private String env;
}