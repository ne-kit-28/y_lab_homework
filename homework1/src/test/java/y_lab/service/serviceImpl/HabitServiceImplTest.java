package y_lab.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.Habit;
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HabitServiceImplTest {

    @Mock
    private HabitRepositoryImpl habitRepository;

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private ProgressRepositoryImpl progressRepository;

    @InjectMocks
    private HabitServiceImpl habitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create habit successfully")
    void createHabitSuccess() {
        Long userId = 1L;
        String name = "Exercise";
        String description = "Daily exercise";
        Frequency frequency = Frequency.DAILY;
        User user = new User();

        when(habitRepository.findByName(name, userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        habitService.createHabit(userId, name, description, frequency);

        verify(habitRepository, times(1)).save(any(Habit.class));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should not create habit if name exists")
    void createHabitNameExists() {
        Long userId = 1L;
        String name = "Exercise";
        String description = "Daily exercise";
        Frequency frequency = Frequency.DAILY;
        Habit existingHabit = new Habit();

        when(habitRepository.findByName(name, userId)).thenReturn(Optional.of(existingHabit));

        habitService.createHabit(userId, name, description, frequency);

        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    @DisplayName("Should delete habit and associated progress")
    void deleteHabitSuccess() {
        Long habitId = 1L;

        habitService.deleteHabit(habitId);

        verify(habitRepository, times(1)).delete(habitId);
        verify(progressRepository, times(1)).deleteAllByHabitId(habitId);
    }

    @Test
    @DisplayName("Should get habits sorted by created date")
    void getHabitsSortedByCreatedDate() {
        Long userId = 1L;
        Habit habit1 = new Habit("Habit1", "Description1", Frequency.DAILY, LocalDate.now().minusDays(1));
        Habit habit2 = new Habit("Habit2", "Description2", Frequency.WEEKLY, LocalDate.now());
        ArrayList<Habit> habits = new ArrayList<>();
        habits.add(habit2);
        habits.add(habit1);

        when(habitRepository.findHabitsByUserId(userId)).thenReturn(Optional.of(habits));

        ArrayList<Habit> result = habitService.getHabits(userId, "date");

        assertThat(result).containsExactly(habit1, habit2);
    }

    @Test
    @DisplayName("Should filter habits by frequency")
    void getHabitsFilteredByFrequency() {
        Long userId = 1L;
        Habit habit1 = new Habit("Habit1", "Description1", Frequency.DAILY, LocalDate.now());
        Habit habit2 = new Habit("Habit2", "Description2", Frequency.WEEKLY, LocalDate.now());
        ArrayList<Habit> habits = new ArrayList<>();
        habits.add(habit1);
        habits.add(habit2);

        when(habitRepository.findHabitsByUserId(userId)).thenReturn(Optional.of(habits));

        ArrayList<Habit> result = habitService.getHabits(userId, Frequency.DAILY);

        assertThat(result).containsExactly(habit1);
    }

    @Test
    @DisplayName("Should update habit successfully")
    void updateHabitSuccess() {
        Long habitId = 1L;
        String newName = "New Habit";
        String newDescription = "New Description";
        Frequency newFrequency = Frequency.WEEKLY;
        User user = new User();
        Habit habit = new Habit("Old Habit", "Old Description", Frequency.DAILY, LocalDate.now());
        habit.setUser(user);

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
        when(habitRepository.findByName(newName, user.getId())).thenReturn(Optional.empty());

        habitService.updateHabit(habitId, newName, newDescription, newFrequency);

        assertThat(habit.getName()).isEqualTo(newName);
        assertThat(habit.getDescription()).isEqualTo(newDescription);
        assertThat(habit.getFrequency()).isEqualTo(newFrequency);
    }

    @Test
    @DisplayName("Should not update habit if new name is already in use")
    void updateHabitNameInUse() {
        Long habitId = 1L;
        String newName = "Existing Habit";
        User user = new User();
        Habit habit = new Habit("Old Habit", "Old Description", Frequency.DAILY, LocalDate.now());
        habit.setUser(user);
        Habit anotherHabit = new Habit();

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
        when(habitRepository.findByName(newName, user.getId())).thenReturn(Optional.of(anotherHabit));

        habitService.updateHabit(habitId, newName, null, null);

        verify(habitRepository, never()).save(any(Habit.class));
    }
}
