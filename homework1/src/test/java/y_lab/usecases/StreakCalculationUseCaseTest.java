package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.domain.entities.Progress;
import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class StreakCalculationUseCaseTest {

    @Mock
    private ProgressRepositoryImpl progressRepository;

    @Mock
    private HabitRepositoryImpl habitRepository;

    @InjectMocks
    private StreakCalculationUseCase streakCalculationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateStreak_shouldReturnZeroStreak_whenNoProgressRecords() {
        Long habitId = 1L;
        Habit habit = new Habit(); // Assume necessary constructors or setters are available
        habit.setId(habitId);
        habit.setFrequency(Frequency.DAILY);

        when(habitRepository.findById(habitId)).thenReturn(java.util.Optional.of(habit));
        when(progressRepository.findByHabitId(habitId)).thenReturn(new ArrayList<>());

        // Act
        streakCalculationUseCase.calculateStreak(habitId);

        // Assert output
        // You might want to capture System.out here to verify the output
        // E.g., using System.setOut() to redirect output and then assert it.
    }

    @Test
    void calculateStreak_shouldReturnStreak_whenProgressRecordsExist() {
        Long habitId = 1L;
        Habit habit = new Habit();
        habit.setId(habitId);
        habit.setFrequency(Frequency.DAILY);

        User user = new User(); // Assuming User has a default constructor
        user.setId(1L); // Assuming User has setId method

        ArrayList<Progress> progressList = new ArrayList<>();
        progressList.add(new Progress(1L, user, habit, LocalDate.now().minusDays(2)));
        progressList.add(new Progress(2L, user, habit, LocalDate.now().minusDays(1)));
        progressList.add(new Progress(3L, user, habit, LocalDate.now()));

        when(habitRepository.findById(habitId)).thenReturn(java.util.Optional.of(habit));
        when(progressRepository.findByHabitId(habitId)).thenReturn(progressList);

        // Act
        streakCalculationUseCase.calculateStreak(habitId);

        // Assert output
        // Capture System.out output here and assert
    }

    @Test
    void calculateStreak_shouldHandleWeeklyFrequency() {
        Long habitId = 2L;
        Habit habit = new Habit();
        habit.setId(habitId);
        habit.setFrequency(Frequency.WEEKLY);

        User user = new User();
        user.setId(1L);

        ArrayList<Progress> progressList = new ArrayList<>();
        progressList.add(new Progress(1L, user, habit, LocalDate.now().minusDays(8))); // 1 week ago
        progressList.add(new Progress(2L, user, habit, LocalDate.now().minusDays(7))); // 1 week ago
        progressList.add(new Progress(3L, user, habit, LocalDate.now())); // today

        when(habitRepository.findById(habitId)).thenReturn(java.util.Optional.of(habit));
        when(progressRepository.findByHabitId(habitId)).thenReturn(progressList);

        // Act
        streakCalculationUseCase.calculateStreak(habitId);

        // Assert output
        // Capture System.out output here and assert
    }
}
