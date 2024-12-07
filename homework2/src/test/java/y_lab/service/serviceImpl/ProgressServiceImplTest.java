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
import y_lab.domain.Habit;
import y_lab.domain.Progress;
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class ProgressServiceImplTest {

    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;
    private final HabitRepositoryImpl habitRepository;
    private final UserRepositoryImpl userRepository;
    private final ProgressRepositoryImpl progressRepository;
    private final ProgressServiceImpl progressService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ProgressServiceImplTest(HabitRepositoryImpl habitRepository, UserRepositoryImpl userRepository, ProgressRepositoryImpl progressRepository, ProgressServiceImpl progressService) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.progressRepository = progressRepository;
        this.progressService = progressService;
    }

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

        userRepository.save(new User("test@example.com", "hashedPassword", "TestUser", false, Role.REGULAR));
        habitRepository.save(new Habit(null, 1L, "sleep", "a lot", Frequency.DAILY, LocalDate.now()));
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
    @DisplayName("Progress must be created")
    public void CreateProgress() throws SQLException {
        Long habitId = 1L;

        progressService.createProgress(habitId);

        Optional<Progress> progress = progressRepository.findByHabitId(habitId).stream().findFirst();
        assertThat(progress).isPresent();
        assertThat(progress.get().getHabitId()).isEqualTo(habitId);
        assertThat(progress.get().getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("статистика должна успешно создаться")
    public void GenerateProgressStatistics() {
        Long habitId = 1L;

        progressService.createProgress(habitId);
        progressService.createProgress(habitId);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        progressService.generateProgressStatistics(habitId, "day");

        String output = outputStream.toString();
        System.setOut(originalOut);

        assertThat(output).contains("Completed: 2");
        assertThat(output).contains("Completion rate: 100%");
    }

    @Test
    @DisplayName("Подсчет повторений подряд")
    public void CalculateStreak() throws SQLException {
        Long userId = 1L;
        Long habitId = 1L;

        progressService.createProgress(habitId);
        progressRepository.save(new Progress(null, userId, habitId, LocalDate.now().minusDays(1)));

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        progressService.calculateStreak(habitId);

        String output = outputStream.toString();
        System.setOut(originalOut);

        assertThat(output).contains("Current streak: 2");
        assertThat(output).contains("Max streak: 2");
    }

    @Test
    @DisplayName("Создание отчета")
    public void GenerateReport() {
        Long habitId = 1L;

        progressService.createProgress(habitId);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        progressService.generateReport(habitId, "day");

        String output = outputStream.toString();
        System.setOut(originalOut);

        assertThat(output).doesNotContain("Sql error");
        assertThat(output).doesNotContain("no habit");
    }
}
