package y_lab.usecases;

import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.NoSuchElementException;

/**
 * Use case for creating a new habit.
 * This class manages the logic for creating a habit
 * associated with a specific user, checking for existing habits
 * with the same name, and saving the new habit to the repository.
 */
public class CreateHabitUseCase {
    private final HabitRepositoryImpl habitRepository;
    private final UserRepositoryImpl userRepository;

    /**
     * Constructs a new {@code CreateHabitUseCase} instance with the
     * specified habit and user repositories.
     *
     * @param habitRepository the repository for managing habits
     * @param userRepository  the repository for managing users
     */
    public CreateHabitUseCase(HabitRepositoryImpl habitRepository, UserRepositoryImpl userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new habit for the specified user.
     * The method checks if a habit with the same name already exists
     * for the user. If it does, the habit creation is aborted.
     * If not, the new habit is created and saved in the repository.
     *
     * @param userId      the ID of the user for whom the habit is being created
     * @param name        the name of the new habit
     * @param description a brief description of the habit
     * @param frequency   the frequency at which the habit should occur
     */
    public void createHabit(Long userId, String name, String description, Frequency frequency) {
        if (habitRepository.findByName(name, userId).isPresent()) {
            System.out.println("Habit with such name exists");
            System.out.println("Habit is not created");
            return;
        }
        Habit habit = new Habit(name, description, frequency, LocalDate.now());
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        habit.setUser(user);
        habitRepository.save(habit);
        System.out.println("Habit " + name + " is created!");
    }
}
