package y_lab.service.serviceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import y_lab.domain.User;
import y_lab.repository.UserRepository;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;

import java.sql.Connection;
import java.sql.DriverManager;
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

    @BeforeEach
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(), postgresContainer.getUsername(), postgresContainer.getPassword());

        UserRepository userRepository = new UserRepositoryImpl(connection);

        loginService = new LoginServiceImpl(userRepository, connection);

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

        loginService.register("John Doe", "john.doe@example.com", "password123");

        User loggedInUser = loginService.login("john.doe@example.com", "password123");

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(loggedInUser.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("несуществующий логин")
    public void testLoginInvalidEmail() {
        User loggedInUser = loginService.login("invalidemail@example.com", "password123");

        assertThat(loggedInUser.getId()).isEqualTo(-1L);
    }

    @Test
    @DisplayName("false при неверном пароле")
    public void testLoginIncorrectPassword() throws SQLException {
        loginService.register("Petr", "petr@ya.com", "password123");

        User loggedInUser = loginService.login("petr@ya.com", "wrongpassword");

        assertThat(loggedInUser.getId()).isEqualTo(-1L);
    }
}
