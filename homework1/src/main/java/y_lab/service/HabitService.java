package y_lab.service;

import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;

import java.util.ArrayList;

/**
 * Interface representing the service for managing habits.
 */
public interface HabitService {

    /**
     * Creates a new habit for a user.
     *
     * @param userId      the ID of the user creating the habit
     * @param name        the name of the habit
     * @param description a brief description of the habit
     * @param frequency   the frequency of the habit (e.g., daily, weekly)
     */
    void createHabit(Long userId, String name, String description, Frequency frequency);

    /**
     * Deletes a habit by its ID.
     *
     * @param id the ID of the habit to be deleted
     */
    void deleteHabit(Long id);

    /**
     * Retrieves a list of habits associated with a user, filtered by the specified criteria.
     *
     * @param userId the ID of the user whose habits are to be retrieved
     * @param filter  the filtering criteria, which can be a String (for sorting) or Frequency
     * @return a list of habits associated with the user
     */
    ArrayList<Habit> getHabits(Long userId, Object filter);

    /**
     * Retrieves a habit by its name for a specific user.
     *
     * @param habitName the name of the habit to retrieve
     * @param userId    the ID of the user
     * @return the ID of the habit if found, otherwise -1
     */
    Long getHabit(String habitName, Long userId);

    /**
     * Updates the details of an existing habit.
     *
     * @param id             the ID of the habit to update
     * @param newName        the new name for the habit (can be null or empty to keep the current name)
     * @param newDescription the new description for the habit (can be null or empty to keep the current description)
     * @param newFrequency   the new frequency for the habit (can be null to keep the current frequency)
     */
    void updateHabit(Long id, String newName, String newDescription, Frequency newFrequency);
}
