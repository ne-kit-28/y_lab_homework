package y_lab.service.serviceImpl;

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
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.util.HashFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");
    private UserServiceImpl userService;
    private Connection connection;
    private final HikariDataSource dataSource = new HikariDataSource();

    @BeforeEach
    public void setUp() throws SQLException {
        dataSource.setJdbcUrl(postgresContainer.getJdbcUrl());
        dataSource.setUsername(postgresContainer.getUsername());
        dataSource.setPassword(postgresContainer.getPassword());

        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(3);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        connection = dataSource.getConnection();

        UserRepositoryImpl userRepository = new UserRepositoryImpl(dataSource);
        HabitRepositoryImpl habitRepository = new HabitRepositoryImpl(dataSource);
        ProgressRepositoryImpl progressRepository = new ProgressRepositoryImpl(dataSource);

        userService = new UserServiceImpl(userRepository, habitRepository, progressRepository);

        CreateSchema.createSchema(connection);

        userRepository.save(new User("test@example.com", "hashedPassword", "TestUser",false, Role.REGULAR));
    }

    @AfterEach
    public void tearDown() throws SQLException {
        connection.createStatement().execute("TRUNCATE TABLE domain.progresses CASCADE;");
        connection.createStatement().execute("TRUNCATE TABLE domain.habits CASCADE;");
        connection.createStatement().execute("TRUNCATE TABLE domain.users CASCADE;");
        connection.createStatement().execute("TRUNCATE TABLE service.admins CASCADE;");

        connection.createStatement().execute("ALTER SEQUENCE domain.user_id_seq RESTART WITH 1;");
        connection.createStatement().execute("ALTER SEQUENCE domain.habit_id_seq RESTART WITH 1;");
        connection.createStatement().execute("ALTER SEQUENCE domain.progress_id_seq RESTART WITH 1;");

        connection.close();
    }

    @Test
    @DisplayName("изменяет пользователя")
    public void editUser() {
        Long userId = 1L;

        User user = User.builder()
                .name("Updated User")
                .email("updated@example.com")
                .passwordHash(HashFunction.hashPassword("newPassword"))
                .build();

        userService.editUser(userId, user);

        Optional<User> updatedUser = userService.getUser("updated@example.com");
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("Updated User");
        assertThat(updatedUser.get().getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.get().getPasswordHash()).isEqualTo(HashFunction.hashPassword("newPassword"));
    }

    @Test
    @DisplayName("удаляет существующего пользователя")
    void deleteUser() {
        Optional<User> deletedUser = userService.getUser("test@example.com");
        userService.deleteUser(deletedUser.get().getId());
        deletedUser = userService.getUser("test@example.com");
        assertThat(deletedUser.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void blockUser() {

        userService.blockUser(1L, true);

        Optional<User> blockedUser = userService.getUser("test@example.com");
        assertThat(blockedUser).isPresent();
        assertThat(blockedUser.get().isBlock()).isTrue();
    }

    @Test
    @DisplayName("Получает список всех пользователей")
    void getUsers() throws SQLException {
        LoginServiceImpl loginService = new LoginServiceImpl(new UserRepositoryImpl(dataSource));
        loginService.register(User.builder()
                .name("User One")
                .email("user1@example.com")
                .passwordHash(HashFunction.hashPassword("hashedPassword1"))
                .build());
        loginService.register(User.builder()
                .name("User Two")
                .email("user2@example.com")
                .passwordHash(HashFunction.hashPassword("hashedPassword2"))
                .build());

        ArrayList<User> users = userService.getUsers();

        assertThat(users.size()).isEqualTo(3);

        assertTrue(users.stream().anyMatch(u -> "test@example.com".equals(u.getEmail())));
        assertTrue(users.stream().anyMatch(u -> "user1@example.com".equals(u.getEmail())));
        assertTrue(users.stream().anyMatch(u -> "user2@example.com".equals(u.getEmail())));
    }
}