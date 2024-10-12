package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.out.repositories.HabitRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GetHabitsUseCaseTest {

    @Mock
    private HabitRepositoryImpl habitRepository;

    @InjectMocks
    private GetHabitsUseCase getHabitsUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHabits_shouldReturnSortedHabits_whenFilterIsString() {
        // Arrange
        Long userId = 1L;
        Habit habit1 = new Habit("Habit 1", "Description 1", Frequency.DAILY, LocalDate.now().minusDays(1));
        Habit habit2 = new Habit("Habit 2", "Description 2", Frequency.WEEKLY, LocalDate.now());
        ArrayList<Habit> habits = new ArrayList<>();
        habits.add(habit2);
        habits.add(habit1);
        when(habitRepository.findHabitsByUserId(userId)).thenReturn(Optional.of(habits));

        // Act
        ArrayList<Habit> result = getHabitsUseCase.getHabits(userId, "sortByDate");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreatedAt()).isBefore(result.get(1).getCreatedAt());
        verify(habitRepository).findHabitsByUserId(userId);
    }

    @Test
    void getHabits_shouldReturnFilteredHabits_whenFilterIsFrequency() {
        // Arrange
        Long userId = 1L;
        Habit habit1 = new Habit("Habit 1", "Description 1", Frequency.DAILY, LocalDate.now());
        Habit habit2 = new Habit("Habit 2", "Description 2", Frequency.WEEKLY, LocalDate.now());
        ArrayList<Habit> habits = new ArrayList<>();
        habits.add(habit1);
        habits.add(habit2);
        when(habitRepository.findHabitsByUserId(userId)).thenReturn(Optional.of(habits));

        // Act
        ArrayList<Habit> result = getHabitsUseCase.getHabits(userId, Frequency.DAILY);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFrequency()).isEqualTo(Frequency.DAILY);
        verify(habitRepository).findHabitsByUserId(userId);
    }

    @Test
    void getHabits_shouldThrowNoSuchElementException_whenNoHabitsFound() {
        // Arrange
        Long userId = 1L;
        when(habitRepository.findHabitsByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getHabitsUseCase.getHabits(userId, "sortByDate"))
                .isInstanceOf(NoSuchElementException.class);
        verify(habitRepository).findHabitsByUserId(userId);
    }

    @Test
    void getHabit_shouldReturnHabitId_whenHabitExists() {
        // Arrange
        Long userId = 1L;
        String habitName = "Habit 1";
        Habit habit = new Habit(habitName, "Description 1", Frequency.DAILY, LocalDate.now());
        habit.setId(1L);
        when(habitRepository.findByName(habitName, userId)).thenReturn(Optional.of(habit));

        // Act
        Long result = getHabitsUseCase.getHabit(habitName, userId);

        // Assert
        assertThat(result).isEqualTo(habit.getId());
        verify(habitRepository).findByName(habitName, userId);
    }

    @Test
    void getHabit_shouldReturnNegativeOne_whenHabitDoesNotExist() {
        // Arrange
        Long userId = 1L;
        String habitName = "Habit 1";
        when(habitRepository.findByName(habitName, userId)).thenReturn(Optional.empty());

        // Act
        Long result = getHabitsUseCase.getHabit(habitName, userId);

        // Assert
        assertThat(result).isEqualTo(-1L);
        verify(habitRepository).findByName(habitName, userId);
    }
}
