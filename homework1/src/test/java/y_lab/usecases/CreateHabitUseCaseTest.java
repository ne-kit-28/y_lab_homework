package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CreateHabitUseCaseTest {
    private HabitRepositoryImpl habitRepository;
    private UserRepositoryImpl userRepository;
    private CreateHabitUseCase createHabitUseCase;

    @BeforeEach
    void setUp() {
        habitRepository = Mockito.mock(HabitRepositoryImpl.class);
        userRepository = Mockito.mock(UserRepositoryImpl.class);
        createHabitUseCase = new CreateHabitUseCase(habitRepository, userRepository);
    }

    @Test
    void createHabit_shouldCreateHabit_whenHabitDoesNotExist() {
        // Arrange
        Long userId = 1L;
        String habitName = "Exercise";
        String description = "Daily exercise routine";
        Frequency frequency = Frequency.DAILY;

        User user = new User(); // Assuming User has a default constructor
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(habitRepository.findByName(habitName, userId)).thenReturn(Optional.empty());

        // Act
        createHabitUseCase.createHabit(userId, habitName, description, frequency);

        // Assert
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void createHabit_shouldNotCreateHabit_whenHabitWithSameNameExists() {
        // Arrange
        Long userId = 1L;
        String habitName = "Exercise";
        String description = "Daily exercise routine";
        Frequency frequency = Frequency.DAILY;

        User user = new User(); // Assuming User has a default constructor
        user.setId(userId);

        Habit existingHabit = new Habit(habitName, description, frequency, LocalDate.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(habitRepository.findByName(habitName, userId)).thenReturn(Optional.of(existingHabit));

        // Act
        createHabitUseCase.createHabit(userId, habitName, description, frequency);

        // Assert
        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    void createHabit_shouldThrowException_whenUserNotFound() {
        // Arrange
        Long userId = 1L;
        String habitName = "Exercise";
        String description = "Daily exercise routine";
        Frequency frequency = Frequency.DAILY;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            createHabitUseCase.createHabit(userId, habitName, description, frequency);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NoSuchElementException.class);
            assertThat(e.getMessage()).isNull();
        }

        verify(habitRepository, never()).save(any(Habit.class));
    }
}
