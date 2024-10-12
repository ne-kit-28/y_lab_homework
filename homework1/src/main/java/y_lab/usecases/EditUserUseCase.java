package y_lab.usecases;

import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;
import y_lab.out.repositories.UserRepositoryImpl;
import y_lab.service.EmailValidatorService;
import y_lab.usecases.utils.HashFunction;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Use case for editing user profiles, deleting users, and blocking/unblocking users.
 * This class manages the logic for updating user information, deleting a user and their
 * associated habits and progress records, and blocking or unblocking a user.
 */
public class EditUserUseCase {
    private final UserRepositoryImpl userRepository;
    private final HabitRepositoryImpl habitRepository;
    private final ProgressRepositoryImpl progressRepository;

    /**
     * Constructs a new {@code EditUserUseCase} instance with the specified
     * user, habit, and progress repositories.
     *
     * @param userRepository     the repository for managing users
     * @param habitRepository    the repository for managing habits
     * @param progressRepository  the repository for managing progress records
     */
    public EditUserUseCase(UserRepositoryImpl userRepository, HabitRepositoryImpl habitRepository, ProgressRepositoryImpl progressRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
        this.progressRepository = progressRepository;
    }

    /**
     * Edits the details of an existing user.
     * This method updates the user's name, email, and password if they are provided.
     * It checks for the uniqueness of the new email and validates the email format.
     *
     * @param id          the ID of the user to be edited
     * @param newName     the new name of the user
     * @param newEmail    the new email of the user
     * @param newPassword  the new password for the user
     */
    public void editUser(Long id, String newName, String newEmail, String newPassword) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            System.out.println("User with this id does not exist!");
            return;
        }

        // Check for unique new email
        if (newEmail != null && !newEmail.isEmpty() && userRepository.findByEmail(newEmail).isPresent()) {
            System.out.println("Email already in use by another account!");
            return;
        }

        if (newEmail != null && !newEmail.isEmpty() && !EmailValidatorService.isValid(newEmail)) {
            System.out.println("Email is incorrect!");
            return;
        }

        if (newName != null && !newName.isEmpty())
            user.get().setName(newName);
        if (newEmail != null && !newEmail.isEmpty())
            user.get().setEmail(newEmail);
        if (newPassword != null && !newPassword.isEmpty())
            user.get().setPasswordHash(HashFunction.hashPassword(newPassword));

        System.out.println("Profile updated successfully!");
    }

    /**
     * Deletes a user and all associated habits and progress records.
     * This method removes the user identified by the specified ID, along with
     * any habits and progress records associated with that user.
     *
     * @param id the ID of the user to be deleted
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        habitRepository.deleteAllByUserId(id);
        progressRepository.deleteAllByUserId(id);
        System.out.println("User and all habits were deleted!");
    }

    /**
     * Blocks or unblocks a user based on the specified ID.
     * This method updates the block status of the user.
     *
     * @param id    the ID of the user to be blocked or unblocked
     * @param block true to block the user, false to unblock
     * @throws IllegalArgumentException if the user ID is null
     * @throws NoSuchElementException    if no user with the specified ID is found
     */
    public void blockUser(Long id, boolean block) {
        // Check for null
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        // Find user by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found."));

        // Block the user
        user.setBlock(block);
    }
}
