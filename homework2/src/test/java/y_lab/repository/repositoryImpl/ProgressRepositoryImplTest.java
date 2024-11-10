package y_lab.repository.repositoryImpl;

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
import y_lab.domain.Progress;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.service.serviceImpl.CreateSchema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ProgressRepositoryImplTest {

    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private Connection connection;
    private final ProgressRepositoryImpl progressRepository;
    private final UserRepositoryImpl userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ProgressRepositoryImplTest(ProgressRepositoryImpl progressRepository, UserRepositoryImpl userRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() throws SQLException {

        connection = jdbcTemplate.getDataSource().getConnection();

        CreateSchema.createSchema(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.prepareStatement("DROP SCHEMA IF EXISTS domain CASCADE;").execute();
        connection.close();
    }

    @Test
    @DisplayName("Сохранение прогресса")
    void testSaveAndFindById() throws SQLException {

        User user = new User(null, "user@example.com", "hash", "User", false, Role.REGULAR, null);
        userRepository.save(user);

        connection.prepareStatement("INSERT INTO domain.habits (user_id, name, description, frequency, created_at) " +
                "VALUES (1, 'Exercise', 'Daily Exercise', 'DAILY', '2024-10-19');").execute();

        Progress progress = new Progress(null, 1L, 1L, LocalDate.now());

        progressRepository.save(progress);

        Optional<Progress> foundProgress = progressRepository.findById(1L);
        assertThat(foundProgress).isPresent();
        assertThat(foundProgress.get().getUserId()).isEqualTo(1L);
        assertThat(foundProgress.get().getHabitId()).isEqualTo(1L);
        assertThat(foundProgress.get().getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Удаление всех выполнений")
    void testDeleteAllByHabitId() throws SQLException {

        User user = new User(null, "user@example.com", "hash", "User", false, Role.REGULAR, null);
        userRepository.save(user);

        connection.prepareStatement("INSERT INTO domain.habits (user_id, name, description, frequency, created_at) " +
                "VALUES (1, 'Exercise', 'Daily Exercise', 'DAILY', '2024-10-19');").execute();

        Progress progress1 = new Progress(null, 1L, 1L, LocalDate.now());
        Progress progress2 = new Progress(null, 1L, 1L, LocalDate.now().minusDays(1));
        progressRepository.save(progress1);
        progressRepository.save(progress2);

        progressRepository.deleteAllByHabitId(1L);

        ResultSet resultSet = connection.prepareStatement("SELECT * FROM domain.progresses WHERE habit_id = 1").executeQuery();
        assertThat(resultSet.next()).isFalse();
    }

    @Test
    @DisplayName("Получение списка выполнений по userId")
    void testFindByHabitId() throws SQLException {

        User user = new User(null, "user@example.com", "hash", "User", false, Role.REGULAR, null);
        userRepository.save(user);

        connection.prepareStatement("INSERT INTO domain.habits (id, user_id, name, description, frequency, created_at) " +
                "VALUES (1, 1, 'Exercise', 'Daily Exercise', 'DAILY', '2024-10-19');").execute();

        Progress progress1 = new Progress(null, 1L, 1L, LocalDate.now());
        Progress progress2 = new Progress(null, 1L, 1L, LocalDate.now().minusDays(1));
        progressRepository.save(progress1);
        progressRepository.save(progress2);

        ArrayList<Progress> progressList = progressRepository.findByHabitId(1L);

        assertThat(progressList.size()).isEqualTo(2);
    }
}
