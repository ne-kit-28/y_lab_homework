package y_lab.repository.repositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import y_lab.domain.Habit;
import y_lab.domain.Progress;
import y_lab.domain.User;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProgressRepositoryImpl Test Suite")
class ProgressRepositoryImplTest {

    private ProgressRepositoryImpl progressRepository;
    private User testUser;
    private Habit testHabit;

    @BeforeEach
    @DisplayName("Set up repository, test user, and test habit")
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
    @DisplayName("Save method should assign an ID and store progress")
    void save() {
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
    @DisplayName("Find by ID should return progress when it exists and isEmpty if not exist")
    void findById() {
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

        // if not exist
        Optional<Progress> foundProgress_ = progressRepository.findById(999L);

        // Assert
        assertThat(foundProgress_).isEmpty();
    }

    @Test
    @DisplayName("Delete all by habit ID should remove all progress for the given habit")
    void deleteAllByHabitId() {
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
    @DisplayName("Delete all by user ID should remove all progress for the given user")
    void deleteAllByUserId() {
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
    @DisplayName("Find by habit ID should return all progress for the given habit")
    void findByHabitId() {
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
}
