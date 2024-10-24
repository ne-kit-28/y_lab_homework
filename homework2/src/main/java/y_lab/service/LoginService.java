package y_lab.service;

import y_lab.domain.User;


/**
 * Interface representing the service for user login and registration.
 */
public interface LoginService {

    /**
     * Logs a user in using their email and password.
     *
     * @param user    the user
     * @return the User object if the login is successful; User object with id = -1 otherwise
     */
    User login(User user);

    void resetPassword(User user);

    /**
     * Initiates a password reset process for the user with the specified email.
     *
     * @param email the email of the user requesting the password reset
     */
    void requestPasswordReset(String email);

    /**
     * Registers a new user with the specified details.
     *
     * @param user is user.
     */
    void register(User user);
}
