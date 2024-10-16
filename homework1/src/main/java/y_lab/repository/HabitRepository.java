package y_lab.repository;

import y_lab.domain.Habit;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface for managing Habit entities and handling persistence operations.
 * Responsible for saving, retrieving, and deleting habits associated with users.
 */
public interface HabitRepository {

    /**
     * Retrieves a Habit by its unique identifier.
     *
     * @param id the unique identifier of the Habit.
     * @return an Optional containing the Habit if found, or an empty Optional if not found.
     */
    Optional<Habit> findById(Long id);

    /**
     * Retrieves a Habit by its name and associated user identifier.
     *
     * @param name   the name of the Habit.
     * @param userId the unique identifier of the user associated with the Habit.
     * @return an Optional containing the Habit if found, or an empty Optional if not found.
     */
    Optional<Habit> findByName(String name, Long userId);

    /**
     * Saves a Habit to the repository.
     * If the Habit already exists, it will be updated; otherwise, it will be created.
     *
     * @param habit the Habit entity to be saved.
     */
    void save(Habit habit);

    /**
     * Deletes a Habit by its unique identifier.
     *
     * @param id the unique identifier of the Habit to be deleted.
     */
    void delete(Long id);

    /**
     * Deletes all habits associated with a specific user.
     *
     * @param userId the unique identifier of the user whose habits should be deleted.
     */
    void deleteAllByUserId(Long userId);

    /**
     * Retrieves all habits associated with a specific user.
     *
     * @param userId the unique identifier of the user.
     * @return an Optional containing a list of Habits associated with the user,
     *         or an empty Optional if no habits are found.
     */
    Optional<ArrayList<Habit>> findHabitsByUserId(Long userId);

    /**
     * Retrieves all habits stored in the repository.
     *
     * @return a list of all Habits in the repository.
     */
    ArrayList<Habit> getAll();
}
