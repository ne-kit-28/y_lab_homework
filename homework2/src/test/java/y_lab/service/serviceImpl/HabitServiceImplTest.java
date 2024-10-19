package y_lab.service.serviceImpl;

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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        habitRepository = new HabitRepositoryImpl(connection);
        ProgressRepositoryImpl progressRepository = new ProgressRepositoryImpl(connection);
        habitService = new HabitServiceImpl(habitRepository, progressRepository, connection);
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

        habitService.createHabit(userId, name, description, frequency);

        ArrayList<Habit> habits = habitService.getHabits(1L, "daily");
        assertEquals(1, habits.size());
        assertEquals(name, habits.get(0).getName());
        assertEquals(description, habits.get(0).getDescription());
    }

    @Test
    @DisplayName("test should delete habit")
    public void DeleteHabit() {
        Long userId = 1L;
        String name = "Habit to delete";
        String description = "This habit will be deleted";
        Frequency frequency = Frequency.WEEKLY;

        habitService.createHabit(userId, name, description, frequency);
        Long habitId = habitService.getHabit("Habit to delete", 1L);
        habitService.deleteHabit(habitId);

        ArrayList<Habit> habits = habitService.getHabits(1L, "daily");
        assertTrue(habits.isEmpty(), "Habit should be deleted");
    }

    @Test
    @DisplayName("Test should return 2 habits")
    public void GetHabits() {
        Long userId = 1L;
        habitService.createHabit(userId, "Habit 1", "Description 1", Frequency.DAILY);
        habitService.createHabit(userId, "Habit 2", "Description 2", Frequency.WEEKLY);

        ArrayList<Habit> habits = habitService.getHabits(userId, "daily");
        assertEquals(2, habits.size());
    }

    @Test
    @DisplayName("Тест на обновление привычки")
    public void UpdateHabit() throws SQLException {
        Long userId = 1L;
        String originalName = "Original Habit";
        String originalDescription = "Original description";
        Frequency originalFrequency = Frequency.DAILY;

        habitService.createHabit(userId, originalName, originalDescription, originalFrequency);

        Long habitId = habitService.getHabit(originalName, userId);

        String newName = "Updated Habit";
        String newDescription = "Updated description";
        Frequency newFrequency = Frequency.WEEKLY;

        habitService.updateHabit(habitId, newName, newDescription, newFrequency);

        Habit updatedHabit = habitRepository.findById(habitId).orElseThrow();

        assertEquals(newName, updatedHabit.getName(), "Habit name should be updated.");
        assertEquals(newDescription, updatedHabit.getDescription(), "Habit description should be updated.");
        assertEquals(newFrequency, updatedHabit.getFrequency(), "Habit frequency should be updated.");
    }

}
