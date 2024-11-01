package y_lab.repository.repositoryImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class HabitRepositoryImplTest {

    @Container
    private PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private Connection connection;
    private HabitRepositoryImpl habitRepository;

    @BeforeEach
    void setUp() throws SQLException {
        String jdbcUrl = postgresContainer.getJdbcUrl();
        String username = postgresContainer.getUsername();
        String password = postgresContainer.getPassword();

        connection = DriverManager.getConnection(jdbcUrl, username, password);
        habitRepository = new HabitRepositoryImpl(connection);

        // Create sequences in the domain schema
        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS domain;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.user_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.habit_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.progress_id_seq;"
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

        // Create admins table in service schema
        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS service;" +
                        "CREATE TABLE IF NOT EXISTS service.admins (" +
                        "id BIGINT PRIMARY KEY, " +
                        "email VARCHAR(255));"
        ).execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.prepareStatement("DROP SCHEMA IF EXISTS domain CASCADE;").execute();
        connection.prepareStatement("DROP SCHEMA IF EXISTS service CASCADE;").execute();
        connection.close();
    }

    @Test
    @DisplayName("Сохраняет и ищет пользователя")
    void testSaveAndFindById() throws SQLException {
        // Insert user
        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();

        // Given
        Habit habit = new Habit(null, 1L, "Exercise", "Daily Exercise", Frequency.DAILY, LocalDate.now());

        // When
        habitRepository.save(habit);

        // Then
        Optional<Habit> foundHabit = habitRepository.findByName("Exercise", 1L);
        assertTrue(foundHabit.isPresent());
        assertEquals("Exercise", foundHabit.get().getName());
        assertEquals("Daily Exercise", foundHabit.get().getDescription());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteHabit() throws SQLException {
        // Insert user
        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();

        // Given
        Habit habit = new Habit(null, 1L, "Read", "Read daily", Frequency.DAILY, LocalDate.now());
        habitRepository.save(habit);

        Optional<Habit> foundHabit = habitRepository.findByName("Read", 1L);
        assertTrue(foundHabit.isPresent());

        // When
        habitRepository.delete(foundHabit.get().getId());

        // Then
        Optional<Habit> deletedHabit = habitRepository.findByName("Read", 1L);
        assertFalse(deletedHabit.isPresent());
    }

    @Test
    @DisplayName("Получение всех привычек пользователя по userId")
    void testFindAllHabits() throws SQLException {
        // Insert user
        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();

        // Given
        Habit habit1 = new Habit(null, 1L, "Exercise", "Daily Exercise", Frequency.DAILY, LocalDate.now());
        Habit habit2 = new Habit(null, 1L, "Read", "Read daily", Frequency.DAILY, LocalDate.now());
        habitRepository.save(habit1);
        habitRepository.save(habit2);

        // When
        Optional<ArrayList<Habit>> userHabits = habitRepository.findHabitsByUserId(1L);

        // Then
        assertTrue(userHabits.isPresent());
        assertEquals(2, userHabits.get().size());
    }

    @Test
    @DisplayName("Проверка, является ли пользователь администратором")
    void testAdminTable() throws SQLException {
        connection.prepareStatement("INSERT INTO service.admins (id, email) VALUES (1, 'admin@test.com');").execute();

        ResultSet resultSet = connection.prepareStatement("SELECT * FROM service.admins WHERE id = 1").executeQuery();

        assertTrue(resultSet.next());
        assertEquals("admin@test.com", resultSet.getString("email"));
    }
}
