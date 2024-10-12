package y_lab.usecases;

import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.out.repositories.HabitRepositoryImpl;

import java.util.Optional;

/**
 * Use case for updating an existing habit.
 * This class manages the updating of a habit's details such as name, description, and frequency.
 */
public class UpdateHabitUseCase {
    private final HabitRepositoryImpl habitRepository;

    /**
     * Constructs a new {@code UpdateHabitUseCase} instance with the specified habit repository.
     *
     * @param habitRepository the repository used for managing habit data
     */
    public UpdateHabitUseCase(HabitRepositoryImpl habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Updates the details of an existing habit identified by its ID.
     * The method checks for the existence of the habit, verifies the uniqueness of the new name,
     * and updates the habit's name, description, and frequency as provided.
     *
     * @param id            the ID of the habit to be updated
     * @param newName      the new name for the habit; can be null or empty if not changing
     * @param newDescription the new description for the habit; can be null or empty if not changing
     * @param newFrequency  the new frequency for the habit; can be null if not changing
     */
    public void updateHabit(Long id, String newName, String newDescription, Frequency newFrequency) {
        Optional<Habit> habit = habitRepository.findById(id);

        if (habit.isEmpty()) {
            System.out.println("Habit with this id does not exist!");
            return;
        }

        // Check for the uniqueness of the new name
        if (newName != null && !newName.isEmpty() && habitRepository.findByName(newName, habit.get().getUser().getId()).isPresent()) {
            System.out.println("Name already in use by another account!");
            return;
        }

        if (newName != null && !newName.isEmpty()) {
            habit.get().setName(newName);
        }
        if (newDescription != null && !newDescription.isEmpty()) {
            habit.get().setDescription(newDescription);
        }
        if (newFrequency != null) {
            habit.get().setFrequency(newFrequency);
        }

        System.out.println("Habit updated successfully!");
    }
}
