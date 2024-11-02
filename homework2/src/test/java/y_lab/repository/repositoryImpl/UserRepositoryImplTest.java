package y_lab.repository.repositoryImpl;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import y_lab.domain.User;
import y_lab.domain.enums.Role;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserRepositoryImplTest {

    @Container
    private PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private Connection connection;
    private final HikariDataSource dataSource = new HikariDataSource();
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource.setJdbcUrl(postgresContainer.getJdbcUrl());
        dataSource.setUsername(postgresContainer.getUsername());
        dataSource.setPassword(postgresContainer.getPassword());

        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(3);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        connection = dataSource.getConnection();

        userRepository = new UserRepositoryImpl(dataSource);

        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS domain;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.user_id_seq;" +
                        "CREATE TABLE IF NOT EXISTS domain.users (" +
                        "id BIGINT PRIMARY KEY DEFAULT nextval('domain.user_id_seq'), " +
                        "email VARCHAR(255), " +
                        "password_hash VARCHAR(255), " +
                        "name VARCHAR(255), " +
                        "is_block BOOLEAN, " +
                        "role VARCHAR(50), " +
                        "reset_token VARCHAR(255));"
        ).execute();

        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS service;" +
                        "CREATE TABLE IF NOT EXISTS service.admins (" +
                        "email VARCHAR(255) PRIMARY KEY);"
        ).execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.prepareStatement("DROP SCHEMA IF EXISTS domain CASCADE;").execute();
        connection.prepareStatement("DROP SCHEMA IF EXISTS service CASCADE;").execute();
        connection.close();
    }

    @Test
    @DisplayName("Сохранение пользователя")
    void SaveAndFindById() throws SQLException {
        User user = new User(null, "testuser@example.com", "hashedpassword", "Test User", false, Role.REGULAR, null);

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(1L);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Проверка существования email")
    void testIsEmailExist() throws SQLException {
        User user = new User(null, "existinguser@example.com", "hashedpassword", "Existing User", false, Role.REGULAR, null);
        userRepository.save(user);

        boolean emailExists = userRepository.isEmailExist("existinguser@example.com");

        assertThat(emailExists).isTrue();
    }

    @Test
    @DisplayName("Проверка пользователя на роль админимтратора")
    void testIsAdminEmail() throws SQLException {
        connection.prepareStatement("INSERT INTO service.admins (email) VALUES ('admin@example.com');").execute();

        boolean isAdminEmail = userRepository.isAdminEmail("admin@example.com");

        assertThat(isAdminEmail).isTrue();
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void testGetAll() throws SQLException {
        User user1 = new User(null, "user1@example.com", "hash1", "User One", false, Role.REGULAR, null);
        User user2 = new User(null, "user2@example.com", "hash2", "User Two", false, Role.REGULAR, null);
        userRepository.save(user1);
        userRepository.save(user2);

        ArrayList<User> users = userRepository.getAll();

        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteById() throws SQLException {
        User user = new User(null, "user@example.com", "hash", "User", false, Role.REGULAR, null);
        userRepository.save(user);

        userRepository.deleteById(1L);

        Optional<User> foundUser = userRepository.findById(1L);
        assertThat(foundUser.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Обновление пользователя")
    void testUpdate() throws SQLException {
        User user = new User(1L, "user@example.com", "hash", "User", false, Role.REGULAR, null);
        userRepository.save(user);

        user.setName("Updated User");
        user.setRole(Role.ADMINISTRATOR);

        userRepository.update(1L, user);

        Optional<User> updatedUser = userRepository.findById(1L);
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("Updated User");
        assertThat(updatedUser.get().getRole()).isEqualTo(Role.ADMINISTRATOR);
    }
}
