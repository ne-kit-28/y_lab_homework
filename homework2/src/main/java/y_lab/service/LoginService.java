package y_lab.service;

import y_lab.domain.User;

import java.sql.SQLException;

/**
 * Interface representing the service for user login and registration.
 */
public interface LoginService {

    /**
     * Logs a user in using their email and password.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return the User object if the login is successful; User object with id = -1 otherwise
     */
    User login(String email, String password);

    void resetPassword(String email, String token, String newPassword);

    /**
     * Initiates a password reset process for the user with the specified email.
     *
     * @param email the email of the user requesting the password reset
     */
    void requestPasswordReset(String email);

    /**
     * Registers a new user with the specified details.
     *
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password for the user account
     */
    void register(String name, String email, String password);
}
