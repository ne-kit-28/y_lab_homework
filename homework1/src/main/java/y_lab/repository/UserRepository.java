package y_lab.repository;

import y_lab.domain.User;

import java.util.ArrayList;
import java.util.Optional;

/**
 * UserRepository is responsible for managing User entities and performing persistence operations.
 * This interface provides methods to save, retrieve, update, and delete users, as well as handle admin emails.
 */
public interface UserRepository {

    /**
     * Checks if a user email already exists in the repository.
     *
     * @param email the email to be checked.
     * @return true if the email exists, false otherwise.
     */
    boolean isEmailExist(String email);

    /**
     * Checks if the given email belongs to an admin user.
     *
     * @param email the email to be checked.
     * @return true if the email is an admin email, false otherwise.
     */
    boolean isAdminEmail(String email);

    /**
     * Saves the provided User entity.
     *
     * @param user the User entity to be saved.
     */
    void save(User user);

    /**
     * Finds a User entity by its email.
     *
     * @param email the email of the User to be found.
     * @return an Optional containing the User entity if found, otherwise an empty Optional.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a User entity by its ID.
     *
     * @param id the ID of the User to be found.
     * @return an Optional containing the User entity if found, otherwise an empty Optional.
     */
    Optional<User> findById(Long id);

    /**
     * Retrieves all User entities from the repository.
     *
     * @return an ArrayList containing all User entities.
     */
    ArrayList<User> getAll();

    /**
     * Deletes a User entity by its ID.
     *
     * @param id the ID of the User to be deleted.
     */
    void deleteById(Long id);

    /**
     * Updates the User entity with the given ID.
     *
     * @param id   the ID of the User to be updated.
     * @param user the User entity containing updated information.
     */
    void update(Long id, User user);
}
