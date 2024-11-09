package y_lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import y_lab.controller.admin.UserAdminController;

@SpringBootApplication
public class HabitApplication {
    public static void main(String[] args) {
        SpringApplication.run(HabitApplication.class, args);
    }
}
