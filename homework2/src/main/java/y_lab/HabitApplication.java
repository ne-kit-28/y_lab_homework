package y_lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import y_lab.audit_logging_spring_boot_starter.util.EnableLogging;
import y_lab.controller.admin.UserAdminController;

@SpringBootApplication
@EnableLogging
public class HabitApplication {
    public static void main(String[] args) {
        SpringApplication.run(HabitApplication.class, args);
    }
}
