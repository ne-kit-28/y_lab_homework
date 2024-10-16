package y_lab.repository.repositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import y_lab.domain.Habit;
import y_lab.domain.User;
import y_lab.domain.enums.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HabitRepositoryImplTest {

    private HabitRepositoryImpl habitRepository;
    private Habit habit;
    private User user;

    @BeforeEach
    void setUp() {

        // Initialize the repository without any file loading
        habitRepository = new HabitRepositoryImpl("");
        habitRepository.setHabits(new HashMap<>());

        // Create a habit for testing
        habit = new Habit();
        habit.setId(1L);
        habit.setName("Exercise");

        user = new User( 1L,"nik@ya.ru", "", "nik", false, Role.ADMINISTRATOR, "");
        habit.setUser(user);

        // Manually add habit to repository's in-memory storage
        habitRepository.getHabits().put(1L, habit);
        habitRepository.setIdGenerated(2L); // Simulate the next generated ID
    }

    @Test
    @DisplayName("Test finds habit by id")
    void testFindById() {
        Optional<Habit> foundHabit = habitRepository.findById(1L);

        assertThat(foundHabit).isPresent();
        assertThat(foundHabit.get().getName()).isEqualTo("Exercise");
    }

    @Test
    @DisplayName("Test save habit to repository")
    void testSaveHabit() {
        Habit newHabit = new Habit();
        newHabit.setName("Reading");

        habitRepository.save(newHabit);

        assertThat(habitRepository.getHabits().size()).isEqualTo(2);
        assertThat(habitRepository.findById(2L)).isPresent();
        assertThat(habitRepository.findById(2L).get().getName()).isEqualTo("Reading");
    }

    @Test
    @DisplayName("test deletes habit from repository")
    void testDeleteHabit() {
        habitRepository.delete(1L);

        assertThat(habitRepository.getHabits().size()).isEqualTo(0);
        assertThat(habitRepository.findById(1L)).isNotPresent();
    }

    @Test
    @DisplayName("Test finds habit by habit's name")
    void testFindByName() {
        Optional<Habit> foundHabit = habitRepository.findByName("Exercise", habit.getUser().getId());

        assertThat(foundHabit).isPresent();
        assertThat(foundHabit.get().getName()).isEqualTo("Exercise");
    }

    @Test
    @DisplayName("delete all habits by user id")
    void testDeleteAllByUserId() {
        Long userId = habit.getUser().getId(); // Get the user ID for the habit

        // Add a second habit for the same user
        Habit anotherHabit = new Habit();
        anotherHabit.setId(2L);
        anotherHabit.setName("Meditation");
        anotherHabit.setUser(habit.getUser()); // Set the same user
        habitRepository.save(anotherHabit);

        // Test delete all habits by user ID
        habitRepository.deleteAllByUserId(userId);

        assertThat(habitRepository.getHabits().size()).isEqualTo(0); // No habits should remain
    }

    @Test
    @DisplayName("find habits by user id")
    void testFindHabitsByUserId() {
        Long userId = habit.getUser().getId(); // Get the user ID for the habit

        Optional<ArrayList<Habit>> foundHabits = habitRepository.findHabitsByUserId(userId);

        assertThat(foundHabits).isPresent();
        assertThat(foundHabits.get().size()).isEqualTo(1);
        assertThat(foundHabits.get().get(0).getName()).isEqualTo("Exercise");
    }

    @Test
    @DisplayName("Get all habits")
    void testGetAllHabits() {
        Habit anotherHabit = new Habit();
        anotherHabit.setId(2L);
        anotherHabit.setName("Reading");

        habitRepository.save(anotherHabit);

        ArrayList<Habit> allHabits = habitRepository.getAll();

        assertThat(allHabits.size()).isEqualTo(2);
        assertThat(allHabits).extracting(Habit::getName).contains("Exercise", "Reading");
    }
}