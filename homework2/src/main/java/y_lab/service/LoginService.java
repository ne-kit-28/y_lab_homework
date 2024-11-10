package y_lab.service;

import y_lab.domain.User;
import y_lab.dto.LoginResponseDto;


/**
 * Interface representing the service for user login and registration.
 */
public interface LoginService {

    /**
     * Logs a user in using their email and password.
     *
     * @param user    the user
     * @return the LoginResponseDto object if the login is successful; LoginResponseDto object with id = -1 otherwise
     */
    LoginResponseDto login(User user);

    /**
     * Reset old password of user/
     *
     * @param user    the user with token and new password
     * @return the LoginResponseDto object if the login is successful; LoginResponseDto object with id = -1 otherwise
     */
    LoginResponseDto resetPassword(User user);

    /**
     * Initiates a password reset process for the user with the specified email.
     *
     * @param email the email of the user requesting the password reset
     * @return true if token was sent and false if not.
     */
    boolean requestPasswordReset(String email);

    /**
     * Registers a new user with the specified details.
     *
     * @param user is user.
     * @return LoginResponseDto object if the register is successful; LoginResponseDto object with id = -1 otherwise
     */
    LoginResponseDto register(User user);
}
