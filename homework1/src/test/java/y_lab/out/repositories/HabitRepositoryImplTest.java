package y_lab.out.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.domain.entities.User;
import y_lab.domain.repositories.HabitRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HabitRepositoryImplTest {

    private HabitRepositoryImpl habitRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a new HabitRepositoryImpl before each test
        habitRepository = new HabitRepositoryImpl("testHabits.ser");

        // Create a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPasswordHash("hashedpassword");
    }

    @Test
    void findById_shouldReturnHabit_whenHabitExists() {
        // Arrange
        Habit habit = new Habit();
        habit.setId(0L);
        habit.setUser(testUser);
        habit.setName("Test Habit");
        habit.setDescription("Description");
        habit.setFrequency(Frequency.DAILY);
        habitRepository.save(habit);

        // Act
        Optional<Habit> foundHabit = habitRepository.findById(0L);

        // Assert
        assertThat(foundHabit).isPresent();
        assertThat(foundHabit.get().getName()).isEqualTo("Test Habit");
    }

    @Test
    void findById_shouldReturnEmpty_whenHabitDoesNotExist() {
        // Act
        Optional<Habit> foundHabit = habitRepository.findById(999L);

        // Assert
        assertThat(foundHabit).isEmpty();
    }

    @Test
    void findByName_shouldReturnHabit_whenHabitExists() {
        // Arrange
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setUser(testUser);
        habit.setName("Unique Habit");
        habit.setDescription("Description");
        habit.setFrequency(Frequency.DAILY);
        habitRepository.save(habit);

        // Act
        Optional<Habit> foundHabit = habitRepository.findByName("Unique Habit", testUser.getId());

        // Assert
        assertThat(foundHabit).isPresent();
        assertThat(foundHabit.get().getName()).isEqualTo("Unique Habit");
    }

    @Test
    void findByName_shouldReturnEmpty_whenHabitDoesNotExist() {
        // Act
        Optional<Habit> foundHabit = habitRepository.findByName("Nonexistent Habit", testUser.getId());

        // Assert
        assertThat(foundHabit).isEmpty();
    }

    @Test
    void delete_shouldRemoveHabit_whenHabitExists() {
        // Arrange
        Habit habit = new Habit();
        habit.setId(2L);
        habit.setUser(testUser);
        habit.setName("Habit to Delete");
        habit.setDescription("Description");
        habit.setFrequency(Frequency.DAILY);
        habitRepository.save(habit);

        // Act
        habitRepository.delete(2L);
        Optional<Habit> foundHabit = habitRepository.findById(2L);

        // Assert
        assertThat(foundHabit).isEmpty();
    }

    @Test
    void deleteAllByUserId_shouldRemoveAllHabitsForUser() {
        // Arrange
        Habit habit1 = new Habit();
        habit1.setId(3L);
        habit1.setUser(testUser);
        habit1.setName("Habit 1");
        habit1.setDescription("Description");
        habit1.setFrequency(Frequency.DAILY);
        habitRepository.save(habit1);

        Habit habit2 = new Habit();
        habit2.setId(4L);
        habit2.setUser(testUser);
        habit2.setName("Habit 2");
        habit2.setDescription("Description");
        habit2.setFrequency(Frequency.DAILY);
        habitRepository.save(habit2);

        // Act
        habitRepository.deleteAllByUserId(testUser.getId());
        Optional<ArrayList<Habit>> userHabits = habitRepository.findHabitsByUserId(testUser.getId());

        // Assert
        assertThat(userHabits).isPresent();
        assertThat(userHabits.get()).isEmpty();
    }

    @Test
    void getAll_shouldReturnAllHabits() {
        // Arrange
        Habit habit1 = new Habit();
        habit1.setId(5L);
        habit1.setUser(testUser);
        habit1.setName("Habit 1");
        habit1.setDescription("Description");
        habit1.setFrequency(Frequency.DAILY);
        habitRepository.save(habit1);

        Habit habit2 = new Habit();
        habit2.setId(6L);
        habit2.setUser(testUser);
        habit2.setName("Habit 2");
        habit2.setDescription("Description");
        habit2.setFrequency(Frequency.WEEKLY);
        habitRepository.save(habit2);

        // Act
        ArrayList<Habit> allHabits = habitRepository.getAll();

        // Assert
        assertThat(allHabits).hasSize(2);
    }

    // Add tests for saveToFile and loadFromFile if needed
}
