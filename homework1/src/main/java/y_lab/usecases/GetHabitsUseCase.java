package y_lab.usecases;

import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.out.repositories.HabitRepositoryImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Use case for retrieving habits associated with a specific user.
 * This class provides functionality to filter and display habits based on various criteria.
 */
public class GetHabitsUseCase {
    private final HabitRepositoryImpl habitRepository;

    /**
     * Constructs a new {@code GetHabitsUseCase} instance with the specified habit repository.
     *
     * @param habitRepository the repository for managing habits
     */
    public GetHabitsUseCase(HabitRepositoryImpl habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Retrieves and displays habits for a specified user, optionally filtering them based on the provided criteria.
     *
     * @param userId the ID of the user whose habits are to be retrieved
     * @param filter the filter to apply; can be a String (for sorting by creation date) or Frequency (for filtering by frequency)
     * @return a list of habits that match the specified criteria
     * @throws NoSuchElementException if no habits are found for the specified user
     */
    public ArrayList<Habit> getHabits(Long userId, Object filter) {
        ArrayList<Habit> habits = habitRepository.findHabitsByUserId(userId).orElseThrow(NoSuchElementException::new);

        if (filter instanceof String) {
            habits = new ArrayList<>(habits.stream()
                    .sorted(Comparator.comparing(Habit::getCreatedAt))
                    .toList());
        } else if (filter instanceof Frequency instanceFilter) {
            habits = new ArrayList<>(habits.stream()
                    .filter(habit -> habit.getFrequency().equals(instanceFilter))
                    .toList());
        }

        if (habits.isEmpty()) {
            System.out.println("No habits");
        } else {
            for (Habit habit : habits) {
                System.out.println("Name: " + habit.getName());
                System.out.println("Description: " + habit.getDescription());
                System.out.println("Created at: " + habit.getCreatedAt());
                System.out.println("Frequency: " + habit.getFrequency().toString());
                System.out.println();
            }
        }

        return habits;
    }

    /**
     * Retrieves a habit by its name for a specified user and displays its details.
     *
     * @param habitName the name of the habit to retrieve
     * @param userId    the ID of the user associated with the habit
     * @return the ID of the retrieved habit, or -1 if the habit is not found
     */
    public Long getHabit(String habitName, Long userId) {
        Optional<Habit> habit = habitRepository.findByName(habitName, userId);
        if (habit.isPresent()) {
            System.out.println("Name: " + habit.get().getName());
            System.out.println("Description: " + habit.get().getDescription());
            System.out.println("Created at: " + habit.get().getCreatedAt());
            System.out.println("Frequency: " + habit.get().getFrequency().toString());
            return habit.get().getId();
        }
        System.out.println("No such habit");
        return -1L;
    }
}
