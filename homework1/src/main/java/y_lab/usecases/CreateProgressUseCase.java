package y_lab.usecases;

import y_lab.domain.entities.Habit;
import y_lab.domain.entities.Progress;
import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;
import y_lab.out.repositories.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.NoSuchElementException;

/**
 * Use case for creating progress records for a specific habit associated with a user.
 * This class manages the logic for recording progress when a user completes a habit.
 */
public class CreateProgressUseCase {
    private final HabitRepositoryImpl habitRepository;
    private final UserRepositoryImpl userRepository;
    private final ProgressRepositoryImpl progressRepository;

    /**
     * Constructs a new {@code CreateProgressUseCase} instance with the
     * specified habit, user, and progress repositories.
     *
     * @param habitRepository    the repository for managing habits
     * @param userRepository     the repository for managing users
     * @param progressRepository  the repository for managing progress records
     */
    public CreateProgressUseCase(HabitRepositoryImpl habitRepository,
                                 UserRepositoryImpl userRepository,
                                 ProgressRepositoryImpl progressRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.progressRepository = progressRepository;
    }

    /**
     * Creates a new progress record for a specific user and habit.
     * This method retrieves the user and habit by their IDs, creates a progress
     * record with the current date, and saves it in the progress repository.
     *
     * @param userId  the ID of the user for whom progress is being recorded
     * @param habitId the ID of the habit for which progress is being recorded
     * @throws NoSuchElementException if no user or habit with the specified IDs is found
     */
    public void createProgress(Long userId, Long habitId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        Habit habit = habitRepository.findById(habitId).orElseThrow(NoSuchElementException::new);
        Progress progress = Progress.builder()
                .user(user)
                .habit(habit)
                .date(LocalDate.now())
                .build();
        progressRepository.save(progress);
        System.out.println("The habit is complete");
    }
}
