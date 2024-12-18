package y_lab.service;

import y_lab.domain.Habit;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface representing the service for managing habits.
 */
public interface HabitService {

    /**
     * Creates a new habit for a user.
     *
     * @param userId      the ID of the user creating the habit
     * @param habit       the Habit
     * @return            id of habit
     */
    Long createHabit(Long userId, Habit habit);

    /**
     * Deletes a habit by its ID.
     *
     * @param id the ID of the habit to be deleted
     * @return               update or not(true/false)
     */
    boolean deleteHabit(Long id);

    /**
     * Retrieves a list of habits associated with a user, filtered by the specified criteria.
     *
     * @param userId the ID of the user whose habits are to be retrieved
     * @param filter  the filtering criteria, which can be a String (for sorting) or Frequency
     * @return a list of habits associated with the user
     */
    ArrayList<Habit> getHabits(Long userId, String filter);

    /**
     * Retrieves a habit by its name for a specific user.
     *
     * @param habitName the name of the habit to retrieve
     * @param userId    the ID of the user
     * @return the Optional of the habit if found, otherwise empty
     */
    Optional<Habit> getHabit(String habitName, Long userId);

    /**
     * Retrieves a habit by its ID.
     *
     * @param habitId    the ID of the habit
     * @return the Optional of the habit if found, otherwise empty
     */
    Optional<Habit> getHabit(Long habitId);
    /**
     * Updates the details of an existing habit.
     *
     * @param id             the ID of the habit to update
     * @param habit       the Habit
     * @return               update or not(true/false)
     */
    boolean updateHabit(Long id, Habit habit);
}
