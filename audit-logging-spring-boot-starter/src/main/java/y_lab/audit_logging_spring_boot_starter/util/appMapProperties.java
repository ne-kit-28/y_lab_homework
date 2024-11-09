package y_lab.audit_logging_spring_boot_starter.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "appmap-starter")
public class appMapProperties {
    private String env;
}