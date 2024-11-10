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
import y_lab.dto.LoginResponseDto;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.util.HashFunction;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class LoginServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    private Connection connection;
    private LoginServiceImpl loginService;
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
        loginService = new LoginServiceImpl(userRepository, dataSource);

        CreateSchema.createSchema(connection);
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
    @DisplayName("login and registration")
    public void Login() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .passwordHash(HashFunction.hashPassword("password123"))
                .build();

        loginService.register(user);

        LoginResponseDto loggedInUser = loginService.login(user);

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.email()).isEqualTo("john.doe@example.com");
        assertThat(loggedInUser.message()).isEqualTo("Successful!");
    }

    @Test
    @DisplayName("несуществующий логин")
    public void testLoginInvalidEmail() {
        User user = User.builder()
                .email("invalidemail@example.com")
                .passwordHash(HashFunction.hashPassword("password123"))
                .build();

        LoginResponseDto loggedInUser = loginService.login(user);

        assertThat(loggedInUser.id()).isEqualTo(-1L);
    }

    @Test
    @DisplayName("false при неверном пароле")
    public void testLoginIncorrectPassword() throws SQLException {
        User user = User.builder()
                .name("Petr")
                .email("petr@ya.com")
                .passwordHash(HashFunction.hashPassword("password123"))
                .build();

        loginService.register(user);

        user.setPasswordHash(HashFunction.hashPassword("wrongpassword"));

        LoginResponseDto loggedInUser = loginService.login(user);

        assertThat(loggedInUser.id()).isEqualTo(-1L);
    }
}
