package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UpdateHabitUseCaseTest {

    @Mock
    private HabitRepositoryImpl habitRepository;

    @InjectMocks
    private UpdateHabitUseCase updateHabitUseCase;

    private Habit habit;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(); // Create a user instance
        user.setId(1L); // Set a dummy user ID

        habit = new Habit(); // Create a habit instance
        habit.setId(1L); // Set a dummy habit ID
        habit.setUser(user);
        habit.setName("Old Habit");
        habit.setDescription("Old Description");
        habit.setFrequency(Frequency.DAILY);
    }

    @Test
    void updateHabit_shouldUpdateSuccessfully_whenHabitExistsAndUniqueName() {
        // Arrange
        Long habitId = 1L;
        String newName = "New Habit";
        String newDescription = "New Description";
        Frequency newFrequency = Frequency.WEEKLY;

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
        when(habitRepository.findByName(newName, user.getId())).thenReturn(Optional.empty());

        // Act
        updateHabitUseCase.updateHabit(habitId, newName, newDescription, newFrequency);

        // Assert
        verify(habitRepository).findById(habitId);
        verify(habitRepository).findByName(newName, user.getId());
        verify(habitRepository, times(0)).save(habit); // Assuming save is called internally
        assertEquals("New Habit", habit.getName());
        assertEquals("New Description", habit.getDescription());
        assertEquals(Frequency.WEEKLY, habit.getFrequency());
        // You might want to capture System.out and assert it as well
    }

    @Test
    void updateHabit_shouldNotUpdate_whenHabitDoesNotExist() {
        // Arrange
        Long habitId = 1L;
        String newName = "New Habit";

        when(habitRepository.findById(habitId)).thenReturn(Optional.empty());

        // Act
        updateHabitUseCase.updateHabit(habitId, newName, null, null);

        // Assert
        verify(habitRepository).findById(habitId);
        verify(habitRepository, times(0)).findByName(anyString(), anyLong());
        // Capture System.out to assert output
    }

    @Test
    void updateHabit_shouldNotUpdate_whenNameIsNotUnique() {
        // Arrange
        Long habitId = 1L;
        String newName = "New Habit";

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
        when(habitRepository.findByName(newName, user.getId())).thenReturn(Optional.of(new Habit()));

        // Act
        updateHabitUseCase.updateHabit(habitId, newName, null, null);

        // Assert
        verify(habitRepository).findById(habitId);
        verify(habitRepository).findByName(newName, user.getId());
        // Capture System.out to assert output
    }

    @Test
    void updateHabit_shouldNotUpdate_whenNewNameIsEmpty() {
        // Arrange
        Long habitId = 1L;

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));

        // Act
        updateHabitUseCase.updateHabit(habitId, "", null, null);

        // Assert
        verify(habitRepository).findById(habitId);
        verify(habitRepository, times(0)).findByName(anyString(), anyLong());
        assertEquals("Old Habit", habit.getName()); // Ensure name remains unchanged
        // Capture System.out to assert output
    }
}
