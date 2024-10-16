package y_lab.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.Habit;
import y_lab.domain.Progress;
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProgressServiceImplTest {

    @Mock
    private HabitRepositoryImpl habitRepository;

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private ProgressRepositoryImpl progressRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private User testUser;
    private Habit testHabit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");

        testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setName("Test Habit");
        testHabit.setUser(testUser);
        testHabit.setFrequency(Frequency.DAILY);
    }

    @Test
    @DisplayName("Should create and save progress successfully")
    void createProgress() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        // Act
        progressService.createProgress(1L, 1L);

        // Assert
        verify(progressRepository).save(any(Progress.class));
    }

    @Test
    @DisplayName("Should generate progress statistics for a given period")
    void generateProgressStatistics() {
        // Arrange
        ArrayList<Progress> progressList = new ArrayList<>();
        Progress progress = new Progress();
        progress.setDate(LocalDate.now().minusDays(1));
        progressList.add(progress);

        when(habitRepository.findById(anyLong())).thenReturn(Optional.of(testHabit));
        when(progressRepository.findByHabitId(anyLong())).thenReturn(progressList);

        // Act
        progressService.generateProgressStatistics(1L, "week");

        // Assert
        verify(progressRepository, times(1)).findByHabitId(1L);
    }

    @Test
    @DisplayName("Should calculate streak correctly")
    void calculateStreak() {
        // Arrange
        ArrayList<Progress> progressList = new ArrayList<>();
        Progress progress1 = new Progress();
        progress1.setDate(LocalDate.now().minusDays(2));
        Progress progress2 = new Progress();
        progress2.setDate(LocalDate.now().minusDays(1));
        progressList.add(progress1);
        progressList.add(progress2);

        when(habitRepository.findById(anyLong())).thenReturn(Optional.of(testHabit));
        when(progressRepository.findByHabitId(anyLong())).thenReturn(progressList);

        // Act
        progressService.calculateStreak(1L);

        // Assert
        verify(progressRepository, times(1)).findByHabitId(1L);
    }

    @Test
    @DisplayName("Should generate full report for the given period")
    void generateReport() {
        Habit testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setName("Test Habit");
        testHabit.setFrequency(Frequency.DAILY);

        ArrayList<Progress> progressList = new ArrayList<>();
        Progress progress = new Progress();
        progress.setDate(LocalDate.now().minusDays(1));
        progressList.add(progress);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
        when(progressRepository.findByHabitId(1L)).thenReturn(progressList);

        // Act
        progressService.generateReport(1L, "week");

        // Assert
        verify(progressRepository, times(2)).findByHabitId(1L);
        verify(habitRepository, times(2)).findById(1L);
    }
}
