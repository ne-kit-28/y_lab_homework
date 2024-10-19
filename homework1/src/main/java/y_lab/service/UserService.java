package y_lab.service;

import y_lab.domain.User;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface representing the service for managing users in the application.
 */
public interface UserService {

    /**
     * Edits the user information based on the provided parameters.
     *
     * @param id          the ID of the user to be edited
     * @param newName     the new name of the user
     * @param newEmail    the new email of the user
     * @param newPassword the new password of the user
     */
    void editUser(Long id, String newName, String newEmail, String newPassword);

    /**
     * Blocks or unblocks a user based on the provided block status.
     *
     * @param id   the ID of the user to be blocked or unblocked
     * @param block true to block the user, false to unblock
     */
    void blockUser(Long id, boolean block);

    /**
     * Deletes a user from the system based on their ID.
     *
     * @param id the ID of the user to be deleted
     */
    void deleteUser(Long id);

    /**
     * Retrieves a user based on their email address.
     *
     * @param email the email of the user to be retrieved
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    Optional<User> getUser(String email);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users
     */
    ArrayList<User> getUsers();
}
