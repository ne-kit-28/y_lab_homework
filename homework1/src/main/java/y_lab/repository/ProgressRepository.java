package y_lab.repository;

import y_lab.domain.Progress;

import java.util.ArrayList;
import java.util.Optional;

/**
 * ProgressRepository is responsible for managing Progress entities and performing persistence operations.
 * This interface provides methods to save, delete, and retrieve progress entries.
 */
public interface ProgressRepository {

    /**
     * Saves the provided Progress entity.
     *
     * @param progress the Progress entity to be saved.
     */
    void save(Progress progress);

    /**
     * Deletes all Progress entities associated with a specific Habit ID.
     *
     * @param habitId the ID of the Habit for which all associated progress entries will be deleted.
     */
    void deleteAllByHabitId(Long habitId);

    /**
     * Deletes all Progress entities associated with a specific User ID.
     *
     * @param userId the ID of the User for whom all progress entries will be deleted.
     */
    void deleteAllByUserId(Long userId);

    /**
     * Finds a Progress entity by its ID.
     *
     * @param progressId the ID of the Progress entity to be found.
     * @return an Optional containing the Progress entity if found, otherwise an empty Optional.
     */
    Optional<Progress> findById(Long progressId);

    /**
     * Finds all Progress entities associated with a specific Habit ID.
     *
     * @param habitId the ID of the Habit whose progress entries are to be found.
     * @return an ArrayList containing all Progress entities related to the specified Habit ID.
     */
    ArrayList<Progress> findByHabitId(Long habitId);
}
