package y_lab.service.serviceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class ProgressServiceImplTest {

    @Container
    private PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;
    private ProgressRepositoryImpl progressRepository;
    private ProgressServiceImpl progressService;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );

        CreateSchema.createSchema(connection);

        UserRepositoryImpl userRepository = new UserRepositoryImpl(connection);
        userRepository.save(new User("test@example.com", "hashedPassword", "TestUser",false, Role.REGULAR));

        HabitRepositoryImpl habitRepository = new HabitRepositoryImpl(connection);
        habitRepository.save(new Habit(null, 1L, "sleep", "a lot", Frequency.DAILY, LocalDate.now()));


        progressRepository = new ProgressRepositoryImpl(connection);
        progressService = new ProgressServiceImpl(habitRepository, progressRepository, connection);
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
        assertTrue(progress.isPresent());
        assertEquals(habitId, progress.get().getHabitId());
        assertEquals(LocalDate.now(), progress.get().getDate());
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

        assertTrue(output.contains("Completed: 2"));
        assertTrue(output.contains("Completion rate: 100%"));
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

        assertTrue(output.contains("Current streak: 2"));
        assertTrue(output.contains("Max streak: 2"));
    }

    @Test
    public void GenerateReport() {
        Long habitId = 1L;

        progressService.createProgress(habitId);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        progressService.generateReport(habitId, "day");

        String output = outputStream.toString();
        System.setOut(originalOut);

        assertFalse(output.contains("Sql error"));
        assertFalse(output.contains("no habit"));
    }
}
