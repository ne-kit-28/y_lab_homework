package y_lab.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy//(proxyTargetClass = true)
@ComponentScan(basePackages = "y_lab")
public class AopConfig {
}

