package y_lab.out.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import y_lab.domain.entities.Habit;
import y_lab.domain.entities.Progress;
import y_lab.domain.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressRepositoryImplTest {

    private ProgressRepositoryImpl progressRepository;
    private User testUser;
    private Habit testHabit;

    @BeforeEach
    void setUp() {
        // Create a new ProgressRepositoryImpl before each test
        progressRepository = new ProgressRepositoryImpl("testProgress.ser");

        // Create a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        // Create a test habit
        testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setUser(testUser);
        testHabit.setName("Test Habit");
    }

    @Test
    void save_shouldAssignIdAndStoreProgress() {
        // Arrange
        Progress progress = new Progress();
        progress.setUser(testUser);
        progress.setHabit(testHabit);

        // Act
        progressRepository.save(progress);

        // Assert
        assertThat(progress.getId()).isEqualTo(0L); // First save should have ID 0
        assertThat(progressRepository.getProgresses()).hasSize(1);
    }

    @Test
    void findById_shouldReturnProgress_whenProgressExists() {
        // Arrange
        Progress progress = new Progress();
        progress.setUser(testUser);
        progress.setHabit(testHabit);
        progressRepository.save(progress);

        // Act
        Optional<Progress> foundProgress = progressRepository.findById(0L);

        // Assert
        assertThat(foundProgress).isPresent();
        assertThat(foundProgress.get().getUser()).isEqualTo(testUser);
    }

    @Test
    void findById_shouldReturnEmpty_whenProgressDoesNotExist() {
        // Act
        Optional<Progress> foundProgress = progressRepository.findById(999L);

        // Assert
        assertThat(foundProgress).isEmpty();
    }

    @Test
    void deleteAllByHabitId_shouldRemoveAllProgressForHabit() {
        // Arrange
        Progress progress1 = new Progress();
        progress1.setId(0L);
        progress1.setUser(testUser);
        progress1.setHabit(testHabit);
        progressRepository.save(progress1);

        Progress progress2 = new Progress();
        progress2.setId(1L);
        progress2.setUser(testUser);
        progress2.setHabit(testHabit);
        progressRepository.save(progress2);

        // Act
        progressRepository.deleteAllByHabitId(testHabit.getId());

        // Assert
        assertThat(progressRepository.getProgresses()).isEmpty();
    }

    @Test
    void deleteAllByUserId_shouldRemoveAllProgressForUser() {
        // Arrange
        Progress progress1 = new Progress();
        progress1.setId(0L);
        progress1.setUser(testUser);
        progress1.setHabit(testHabit);
        progressRepository.save(progress1);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setName("Another User");

        Progress progress2 = new Progress();
        progress2.setId(1L);
        progress2.setUser(anotherUser);
        progress2.setHabit(testHabit);
        progressRepository.save(progress2);

        // Act
        progressRepository.deleteAllByUserId(testUser.getId());

        // Assert
        assertThat(progressRepository.getProgresses()).hasSize(1);
        assertThat(progressRepository.getProgresses().values().iterator().next().getUser()).isEqualTo(anotherUser);
    }

    @Test
    void findByHabitId_shouldReturnAllProgressForHabit() {
        // Arrange
        Progress progress1 = new Progress();
        progress1.setId(0L);
        progress1.setUser(testUser);
        progress1.setHabit(testHabit);
        progressRepository.save(progress1);

        Progress progress2 = new Progress();
        progress2.setId(1L);
        progress2.setUser(testUser);
        progress2.setHabit(testHabit);
        progressRepository.save(progress2);

        // Act
        ArrayList<Progress> foundProgresses = progressRepository.findByHabitId(testHabit.getId());

        // Assert
        assertThat(foundProgresses).hasSize(2);
    }

    // Add tests for saveToFile and loadFromFile if needed
}
