package y_lab.usecases;

import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;

/**
 * Use case for deleting a habit and its associated progress records.
 * This class manages the logic for removing a habit and ensuring that
 * any related progress records are also deleted from the progress repository.
 */
public class DeleteHabitUseCase {
    private final HabitRepositoryImpl habitRepository;
    private final ProgressRepositoryImpl progressRepository;

    /**
     * Constructs a new {@code DeleteHabitUseCase} instance with the
     * specified habit and progress repositories.
     *
     * @param habitRepository    the repository for managing habits
     * @param progressRepository  the repository for managing progress records
     */
    public DeleteHabitUseCase(HabitRepositoryImpl habitRepository, ProgressRepositoryImpl progressRepository) {
        this.habitRepository = habitRepository;
        this.progressRepository = progressRepository;
    }

    /**
     * Deletes a habit and all its associated progress records.
     * This method removes the habit identified by the specified ID
     * and also deletes all progress records linked to that habit.
     *
     * @param id the ID of the habit to be deleted
     */
    public void deleteHabit(Long id) {
        habitRepository.delete(id);
        progressRepository.deleteAllByHabitId(id);
        System.out.println("Habit with id: " + id + " was deleted!");
    }
}
