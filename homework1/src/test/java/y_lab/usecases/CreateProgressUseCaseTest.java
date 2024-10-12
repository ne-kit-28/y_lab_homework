package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import y_lab.domain.entities.Habit;
import y_lab.domain.entities.Progress;
import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;
import y_lab.out.repositories.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CreateProgressUseCaseTest {

    private HabitRepositoryImpl habitRepository;
    private UserRepositoryImpl userRepository;
    private ProgressRepositoryImpl progressRepository;
    private CreateProgressUseCase createProgressUseCase;

    @BeforeEach
    void setUp() {
        habitRepository = mock(HabitRepositoryImpl.class);
        userRepository = mock(UserRepositoryImpl.class);
        progressRepository = mock(ProgressRepositoryImpl.class);
        createProgressUseCase = new CreateProgressUseCase(habitRepository, userRepository, progressRepository);
    }

    @Test
    void createProgress_shouldCreateNewProgress_whenUserAndHabitExist() {
        // Arrange
        Long userId = 1L;
        Long habitId = 1L;

        User user = new User();
        user.setId(userId);

        Habit habit = new Habit();
        habit.setId(habitId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));

        // Act
        createProgressUseCase.createProgress(userId, habitId);

        // Assert
        ArgumentCaptor<Progress> progressCaptor = ArgumentCaptor.forClass(Progress.class);
        verify(progressRepository, times(1)).save(progressCaptor.capture());

        Progress createdProgress = progressCaptor.getValue();
        assertThat(createdProgress.getUser()).isEqualTo(user);
        assertThat(createdProgress.getHabit()).isEqualTo(habit);
        assertThat(createdProgress.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void createProgress_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        Long habitId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> {
            createProgressUseCase.createProgress(userId, habitId);
        }).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void createProgress_shouldThrowException_whenHabitDoesNotExist() {
        // Arrange
        Long userId = 1L;
        Long habitId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(habitRepository.findById(habitId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> {
            createProgressUseCase.createProgress(userId, habitId);
        }).isInstanceOf(NoSuchElementException.class);
    }
}
