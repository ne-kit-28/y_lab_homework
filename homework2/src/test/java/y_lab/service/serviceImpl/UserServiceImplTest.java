package y_lab.service.serviceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.util.HashFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class UserServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    private final UserServiceImpl userService;
    private final UserRepositoryImpl userRepository;
    private final LoginServiceImpl loginService;
    private Connection connection;

    @Autowired
    UserServiceImplTest(UserServiceImpl userService, UserRepositoryImpl userRepository, LoginServiceImpl loginService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.loginService = loginService;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    public void setUp() throws SQLException {
        connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();

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

        userService.blockUser(4L, true);

        Optional<User> blockedUser = userService.getUser("test@example.com");
        assertThat(blockedUser).isPresent();
        assertThat(blockedUser.get().isBlock()).isTrue();
    }

    @Test
    @DisplayName("Получает список всех пользователей")
    void getUsers(){
        loginService.register(User.builder()
                .name("User One")
                .email("us1@example.com")
                .passwordHash(HashFunction.hashPassword("hashedPassword1"))
                .build());
        loginService.register(User.builder()
                .name("User Two")
                .email("us2@example.com")
                .passwordHash(HashFunction.hashPassword("hashedPassword2"))
                .build());

        ArrayList<User> users = userService.getUsers();

        assertThat(users.size()).isEqualTo(3);

        assertTrue(users.stream().anyMatch(u -> "test@example.com".equals(u.getEmail())));
        assertTrue(users.stream().anyMatch(u -> "us1@example.com".equals(u.getEmail())));
        assertTrue(users.stream().anyMatch(u -> "us2@example.com".equals(u.getEmail())));
    }
}