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
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest
public class HabitServiceImplTest {

    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;
    private final HabitRepositoryImpl habitRepository;
    private final HabitServiceImpl habitService;
    private final UserRepositoryImpl userRepository;

    @Autowired
    public HabitServiceImplTest(HabitRepositoryImpl habitRepository, HabitServiceImpl habitService, UserRepositoryImpl userRepository) {
        this.habitRepository = habitRepository;
        this.habitService = habitService;
        this.userRepository = userRepository;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        userRepository.save(
                new User("test@example.com", "hashedPassword", "TestUser", false, Role.REGULAR));
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
