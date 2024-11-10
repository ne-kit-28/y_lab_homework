package y_lab;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import y_lab.audit_logging_spring_boot_starter.annotation.EnableLogging;


@SpringBootApplication
@EnableLogging
@OpenAPIDefinition(info = @Info(title = "My API", version = "v1", description = "API for My Project"))
public class HabitApplication {
    public static void main(String[] args) {
        SpringApplication.run(HabitApplication.class, args);
    }
}
