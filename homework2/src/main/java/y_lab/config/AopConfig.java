package y_lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import y_lab.out.audit.AuditAndLoggingAspect;
import y_lab.out.audit.AuditServiceImpl;

@Configuration
@EnableAspectJAutoProxy//(proxyTargetClass = true)
@ComponentScan(basePackages = "y_lab")
public class AopConfig {
}

