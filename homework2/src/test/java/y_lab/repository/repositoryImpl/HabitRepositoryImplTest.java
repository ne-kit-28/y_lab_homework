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
import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;
import y_lab.service.serviceImpl.CreateSchema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class HabitRepositoryImplTest {

    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private Connection connection;
    private final HabitRepositoryImpl habitRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public HabitRepositoryImplTest(HabitRepositoryImpl habitRepository) {
        this.habitRepository = habitRepository;
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
        connection.prepareStatement("DROP SCHEMA IF EXISTS service CASCADE;").execute();
        connection.close();
    }

    @Test
    @DisplayName("Сохраняет и ищет привычки")
    void testSaveAndFindById() throws SQLException {

        connection.prepareStatement("INSERT INTO domain.users (name) VALUES ('testuser');").execute();

        Habit habit = new Habit(null, 1L, "Exercise", "Daily Exercise", Frequency.DAILY, LocalDate.now());

        habitRepository.save(habit);

        Optional<Habit> foundHabit = habitRepository.findByName("Exercise", 1L);
        assertThat(foundHabit).isPresent();
        assertThat(foundHabit.get().getName()).isEqualTo("Exercise");
        assertThat(foundHabit.get().getDescription()).isEqualTo("Daily Exercise");
    }

    @Test
    @DisplayName("Удаление привычки")
    void testDeleteHabit() throws SQLException {

        connection.prepareStatement("INSERT INTO domain.users (id, email, password_hash, name, is_block, role) " +
                "VALUES (nextval('domain.user_id_seq'), 'user10@example.com', 'hashed_password_1', 'testuser', 'false', 'REGULAR');").execute();

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

        connection.prepareStatement("INSERT INTO domain.users (id, email, password_hash, name, is_block, role) " +
                "VALUES (nextval('domain.user_id_seq'), 'user10@example.com', 'hashed_password_1', 'testuser', 'false', 'REGULAR');").execute();

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
