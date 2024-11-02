package y_lab.service.serviceImpl;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import y_lab.domain.Habit;
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
public class HabitServiceImplTest {

    @Container
    private PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;
    private HabitRepositoryImpl habitRepository;
    private HabitServiceImpl habitService;
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

        CreateSchema.createSchema(connection);

        UserRepositoryImpl userRepository = new UserRepositoryImpl(dataSource);
        userRepository.save(new User("test@example.com", "hashedPassword", "TestUser", false, Role.REGULAR));
        habitRepository = new HabitRepositoryImpl(dataSource);
        ProgressRepositoryImpl progressRepository = new ProgressRepositoryImpl(dataSource);
        habitService = new HabitServiceImpl(habitRepository, progressRepository, dataSource);
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
    @DisplayName("create habit")
    public void CreateHabit() {
        Long userId = 1L;
        String name = "Test Habit";
        String description = "Testing habit creation";
        Frequency frequency = Frequency.DAILY;
        Habit habit = Habit.builder()
                .name(name)
                .description(description)
                .frequency(frequency)
                .build();

        habitService.createHabit(userId, habit);

        ArrayList<Habit> habits = habitService.getHabits(1L, "daily");
        assertThat(habits).hasSize(1);
        assertThat(habits.get(0).getName()).isEqualTo(name);
        assertThat(habits.get(0).getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("test should delete habit")
    public void DeleteHabit() {
        Long userId = 1L;
        String name = "Habit to delete";
        String description = "This habit will be deleted";
        Frequency frequency = Frequency.WEEKLY;

        Habit habit = Habit.builder()
                .name(name)
                .description(description)
                .frequency(frequency)
                .build();

        habitService.createHabit(userId, habit);
        Optional<Habit> opHabit = habitService.getHabit("Habit to delete", 1L);
        habitService.deleteHabit(opHabit.get().getId());

        ArrayList<Habit> habits = habitService.getHabits(1L, "daily");
        assertThat(habits).isEmpty();
    }

    @Test
    @DisplayName("Test should return 2 habits")
    public void GetHabits() {
        Long userId = 1L;

        habitService.createHabit(userId, Habit.builder()
                .name("Habit 1")
                .description("Description 1")
                .frequency(Frequency.DAILY)
                .build());
        habitService.createHabit(userId, Habit.builder()
                .name("Habit 2")
                .description("Description 2")
                .frequency(Frequency.DAILY)
                .build());

        ArrayList<Habit> habits = habitService.getHabits(userId, "daily");
        assertThat(habits).hasSize(2);
    }

    @Test
    @DisplayName("Тест на обновление привычки")
    public void UpdateHabit() throws SQLException {
        Long userId = 1L;
        String originalName = "Original Habit";
        String originalDescription = "Original description";
        Frequency originalFrequency = Frequency.DAILY;

        Habit oldHabit = Habit.builder()
                .name(originalName)
                .description(originalDescription)
                .frequency(originalFrequency)
                .build();

        habitService.createHabit(userId, oldHabit);

        Optional<Habit> opHabit = habitService.getHabit(originalName, userId);

        String newName = "Updated Habit";
        String newDescription = "Updated description";
        Frequency newFrequency = Frequency.WEEKLY;

        Habit newHabit = Habit.builder()
                .name(newName)
                .description(newDescription)
                .frequency(newFrequency)
                .build();

        habitService.updateHabit(opHabit.get().getId(), newHabit);

        Habit updatedHabit = habitRepository.findById(opHabit.get().getId()).orElseThrow();

        assertThat(updatedHabit.getName()).isEqualTo(newName);
        assertThat(updatedHabit.getDescription()).isEqualTo(newDescription);
        assertThat(updatedHabit.getFrequency()).isEqualTo(newFrequency);
    }
}
