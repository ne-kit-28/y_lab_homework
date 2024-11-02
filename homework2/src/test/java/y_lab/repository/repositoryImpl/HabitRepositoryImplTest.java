package y_lab.repository.repositoryImpl;

import com.zaxxer.hikari.HikariDataSource;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
public class HabitRepositoryImplTest {

    @Container
    private PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private Connection connection;
    private HabitRepositoryImpl habitRepository;

    private final HikariDataSource dataSource = new HikariDataSource();

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
        habitRepository = new HabitRepositoryImpl(dataSource);

        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS domain;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.user_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.habit_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.progress_id_seq;"
        ).execute();

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

        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();

        Habit habit = new Habit(null, 1L, "Exercise", "Daily Exercise", Frequency.DAILY, LocalDate.now());

        habitRepository.save(habit);

        Optional<Habit> foundHabit = habitRepository.findByName("Exercise", 1L);
        assertThat(foundHabit).isPresent();
        assertThat(foundHabit.get().getName()).isEqualTo("Exercise");
        assertThat(foundHabit.get().getDescription()).isEqualTo("Daily Exercise");
    }

    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteHabit() throws SQLException {

        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();

        Habit habit = new Habit(null, 1L, "Read", "Read daily", Frequency.DAILY, LocalDate.now());
        habitRepository.save(habit);

        Optional<Habit> foundHabit = habitRepository.findByName("Read", 1L);
        assertThat(foundHabit).isPresent();

        habitRepository.delete(foundHabit.get().getId());

        Optional<Habit> deletedHabit = habitRepository.findByName("Read", 1L);
        assertThat(deletedHabit).isNotPresent();
    }

    @Test
    @DisplayName("Получение всех привычек пользователя по userId")
    void testFindAllHabits() throws SQLException {

        connection.prepareStatement("INSERT INTO domain.users (username) VALUES ('testuser');").execute();

        Habit habit1 = new Habit(null, 1L, "Exercise", "Daily Exercise", Frequency.DAILY, LocalDate.now());
        Habit habit2 = new Habit(null, 1L, "Read", "Read daily", Frequency.DAILY, LocalDate.now());
        habitRepository.save(habit1);
        habitRepository.save(habit2);

        Optional<ArrayList<Habit>> userHabits = habitRepository.findHabitsByUserId(1L);

        assertThat(userHabits).isPresent();
        assertThat(userHabits.get()).hasSize(2);
    }

    @Test
    @DisplayName("Проверка, является ли пользователь администратором")
    void testAdminTable() throws SQLException {
        connection.prepareStatement("INSERT INTO service.admins (id, email) VALUES (1, 'admin@test.com');").execute();

        ResultSet resultSet = connection.prepareStatement("SELECT * FROM service.admins WHERE id = 1").executeQuery();

        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getString("email")).isEqualTo("admin@test.com");
    }
}
