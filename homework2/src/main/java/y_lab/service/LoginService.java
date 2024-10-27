package y_lab.service;

import y_lab.domain.User;
import y_lab.dto.LoginResponseDto;
import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;


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
    @LogExecutionTime
    @AuditAction(action = "Авторизации")
    LoginResponseDto login(User user);

    @LogExecutionTime
    @AuditAction(action = "Сброс пароля")
    LoginResponseDto resetPassword(User user);

    /**
     * Initiates a password reset process for the user with the specified email.
     *
     * @param email the email of the user requesting the password reset
     */
    @LogExecutionTime
    @AuditAction(action = "Запрос сброса пароля")
    boolean requestPasswordReset(String email);

    /**
     * Registers a new user with the specified details.
     *
     * @param user is user.
     */
    @LogExecutionTime
    @AuditAction(action = "Регистрация")
    LoginResponseDto register(User user);
}
