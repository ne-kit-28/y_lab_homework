package y_lab.repository.repositoryImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import y_lab.domain.Progress;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class ProgressRepositoryImplTest {

    @Container
    private PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private Connection connection;
    private ProgressRepositoryImpl progressRepository;

    @BeforeEach
    void setUp() throws SQLException {
        String jdbcUrl = postgresContainer.getJdbcUrl();
        String username = postgresContainer.getUsername();
        String password = postgresContainer.getPassword();

        connection = DriverManager.getConnection(jdbcUrl, username, password);
        progressRepository = new ProgressRepositoryImpl(connection);

        // Create sequences in the domain schema
        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS domain;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.progress_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.user_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.habit_id_seq;"
        ).execute();

        // Create tables in the domain schema
        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS domain.users (" +
                        "id BIGINT PRIMARY KEY DEFAULT nextval('domain.user_id_seq'), " +
                        "username VARCHAR(255));" +

                        "CREATE TABLE IF NOT EXISTS domain.habits (" +
                        "id BIGINT PRIMARY KEY DEFAULT nextval('domain.habit_id_seq'), " +
                        "user_id BIGINT, " +
                        "name VARCHAR(64), " +
                        "description VARCHAR(128), " +
                        "frequency VARCHAR(16), " +
                        "created_at VARCHAR(32), " +
                        "FOREIGN KEY (user_id) REFERENCES domain.users(id));" +

                        "CREATE TABLE IF NOT EXISTS domain.progresses (" +
                        "id BIGINT PRIMARY KEY DEFAULT nextval('domain.progress_id_seq'), " +
                        "user_id BIGINT, " +
                        "habit_id BIGINT, " +
                        "date VARCHAR(32), " +
                        "FOREIGN KEY (habit_id) REFERENCES domain.habits(id), " +
                        "FOREIGN KEY (user_id) REFERENCES domain.users(id));"
        ).execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.prepareStatement("DROP SCHEMA IF EXISTS domain CASCADE;").execute();
        connection.close();
    }

    @Test
    void testSaveAndFindById() throws SQLException {
        // Insert user and habit
        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();
        connection.prepareStatement("INSERT INTO domain.habits (user_id, name, description, frequency, created_at) " +
                "VALUES (1, 'Exercise', 'Daily Exercise', 'DAILY', '2024-10-19');").execute();

        // Given
        Progress progress = new Progress(null, 1L, 1L, LocalDate.now());

        // When
        progressRepository.save(progress);

        // Then
        Optional<Progress> foundProgress = progressRepository.findById(1L);
        assertTrue(foundProgress.isPresent());
        assertEquals(1L, foundProgress.get().getUserId());
        assertEquals(1L, foundProgress.get().getHabitId());
        assertEquals(LocalDate.now(), foundProgress.get().getDate());
    }

    @Test
    void testDeleteAllByHabitId() throws SQLException {
        // Insert user and habit
        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();
        connection.prepareStatement("INSERT INTO domain.habits (user_id, name, description, frequency, created_at) " +
                "VALUES (1, 'Exercise', 'Daily Exercise', 'DAILY', '2024-10-19');").execute();

        // Insert progress entries
        Progress progress1 = new Progress(null, 1L, 1L, LocalDate.now());
        Progress progress2 = new Progress(null, 1L, 1L, LocalDate.now().minusDays(1));
        progressRepository.save(progress1);
        progressRepository.save(progress2);

        // When
        progressRepository.deleteAllByHabitId(1L);

        // Then
        ResultSet resultSet = connection.prepareStatement("SELECT * FROM domain.progresses WHERE habit_id = 1").executeQuery();
        assertFalse(resultSet.next());
    }

    @Test
    void testFindByHabitId() throws SQLException {
        // Insert user and habit
        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();
        connection.prepareStatement("INSERT INTO domain.habits (user_id, name, description, frequency, created_at) " +
                "VALUES (1, 'Exercise', 'Daily Exercise', 'DAILY', '2024-10-19');").execute();

        // Insert progress entries
        Progress progress1 = new Progress(null, 1L, 1L, LocalDate.now());
        Progress progress2 = new Progress(null, 1L, 1L, LocalDate.now().minusDays(1));
        progressRepository.save(progress1);
        progressRepository.save(progress2);

        // When
        ArrayList<Progress> progressList = progressRepository.findByHabitId(1L);

        // Then
        assertEquals(2, progressList.size());
    }
}
