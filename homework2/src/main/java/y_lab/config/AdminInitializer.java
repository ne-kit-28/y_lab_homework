package y_lab.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.util.HashFunction;

import java.sql.SQLException;
import java.util.Optional;

@Component
public class AdminInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepositoryImpl userRepository;

    @Autowired
    public AdminInitializer(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@admin.ru";
        try {
            Optional<User> existing = userRepository.findByEmail(adminEmail);
            if (existing.isPresent()) {
                return; // already exists
            }
        } catch (SQLException e) {
            logger.warn("Admin seeding skipped due to DB access issue: {}", e.getMessage());
            return;
        }

        User admin = new User(
                adminEmail,
                HashFunction.hashPassword("admin123"),
                "Admin",
                false,
                Role.ADMINISTRATOR
        );
        try {
            userRepository.save(admin);
            logger.info("Seeded admin user: {}", adminEmail);
        } catch (SQLException e) {
            logger.warn("Failed to seed admin user: {}", e.getMessage());
        }
    }
}
