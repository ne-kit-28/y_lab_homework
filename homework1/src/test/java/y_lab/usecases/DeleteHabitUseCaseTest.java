package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;

import static org.mockito.Mockito.*;

class DeleteHabitUseCaseTest {

    private HabitRepositoryImpl habitRepository;
    private ProgressRepositoryImpl progressRepository;
    private DeleteHabitUseCase deleteHabitUseCase;

    @BeforeEach
    void setUp() {
        habitRepository = mock(HabitRepositoryImpl.class);
        progressRepository = mock(ProgressRepositoryImpl.class);
        deleteHabitUseCase = new DeleteHabitUseCase(habitRepository, progressRepository);
    }

    @Test
    void deleteHabit_shouldDeleteHabitAndProgressRecords() {
        // Arrange
        Long habitId = 1L;

        // Act
        deleteHabitUseCase.deleteHabit(habitId);

        // Assert
        verify(habitRepository, times(1)).delete(habitId);
        verify(progressRepository, times(1)).deleteAllByHabitId(habitId);
    }

    @Test
    void deleteHabit_shouldNotThrowException_whenHabitDoesNotExist() {
        // Arrange
        Long habitId = 1L;

        doNothing().when(habitRepository).delete(habitId);
        doNothing().when(progressRepository).deleteAllByHabitId(habitId);

        // Act & Assert
        deleteHabitUseCase.deleteHabit(habitId);

        verify(habitRepository, times(1)).delete(habitId);
        verify(progressRepository, times(1)).deleteAllByHabitId(habitId);
    }
}
