package y_lab.service;

import y_lab.domain.User;
import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;

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
     * @param user        the user
     */
    @LogExecutionTime
    @AuditAction(action = "Редактирование пользователя")
    boolean editUser(Long id, User user);

    /**
     * Blocks or unblocks a user based on the provided block status.
     *
     * @param id   the ID of the user to be blocked or unblocked
     * @param block true to block the user, false to unblock
     */
    @LogExecutionTime
    @AuditAction(action = "(де-)блокировка пользователя")
    boolean blockUser(Long id, boolean block);

    /**
     * Deletes a user from the system based on their ID.
     *
     * @param id the ID of the user to be deleted
     */
    @LogExecutionTime
    @AuditAction(action = "Удаление пользователя")
    boolean deleteUser(Long id);

    /**
     * Retrieves a user based on their email address.
     *
     * @param email the email of the user to be retrieved
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    @LogExecutionTime
    @AuditAction(action = "Получение пользователя по email")
    Optional<User> getUser(String email);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users
     */
    @LogExecutionTime
    @AuditAction(action = "Получение всех пользователей")
    ArrayList<User> getUsers();
}
