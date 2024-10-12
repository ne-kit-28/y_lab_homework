package y_lab.usecases;

import y_lab.domain.entities.User;
import y_lab.out.repositories.UserRepositoryImpl;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import static y_lab.usecases.utils.HashFunction.hashPassword;

/**
 * Use case for handling password reset requests and operations.
 * This class is responsible for generating reset tokens, validating them,
 * and resetting user passwords.
 */
public class PasswordResetUseCase {
    private final UserRepositoryImpl userRepository;

    /**
     * Constructs a new {@code PasswordResetUseCase} instance with the specified user repository.
     *
     * @param userRepository the repository for managing users
     */
    public PasswordResetUseCase(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Initiates a password reset request by generating a token and sending it via email (simulation).
     * Prompts the user to enter the token and new password to complete the reset process.
     *
     * @param email the email of the user requesting the password reset
     */
    public void requestPasswordReset(String email) {
        Scanner scanner = new Scanner(System.in);

        // Find user by email
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            System.out.println("User with this email does not exist!");
            return;
        }

        // Generate password reset token
        String token = generateResetToken();
        user.get().setResetToken(token);

        // Simulate email sending (actual app would send an email here)
        sendResetEmail(user.get().getEmail(), token, "reset token: ");

        System.out.println("Password reset token sent to your email.");

        // Collect token and new password input
        System.out.println("Enter your token:");
        String readToken = scanner.nextLine();
        System.out.println("Enter your new password:");
        String password = scanner.nextLine();

        resetPassword(email, readToken, password);
    }

    /**
     * Resets the user's password after verifying the provided token.
     *
     * @param email       the email of the user resetting the password
     * @param token       the reset token provided by the user
     * @param newPassword the new password to set for the user
     */
    public void resetPassword(String email, String token, String newPassword) {

        // Find user by email
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            System.out.println("User with this email does not exist!");
            return;
        }

        // Validate the reset token
        if (!token.equals(user.get().getResetToken())) {
            System.out.println("Invalid token!");
            return;
        }

        // Reset password and clear token
        user.get().setPasswordHash(hashPassword(newPassword));
        user.get().setResetToken(null);

        // Simulate email sending (actual app would send an email here)
        sendResetEmail(user.get().getEmail(), newPassword, "new password: ");

        System.out.println("Password has been successfully reset!");
    }

    /**
     * Generates a unique token for password reset operations.
     *
     * @return a unique UUID token
     */
    private String generateResetToken() {
        return UUID.randomUUID().toString(); // Generate a unique UUID token
    }

    /**
     * Simulates sending an email with a password reset token or new password.
     *
     * @param email   the recipient's email address
     * @param token   the reset token or new password to include in the email
     * @param element the element being sent (e.g., "reset token", "new password")
     */
    private void sendResetEmail(String email, String token, String element) {
        // In an actual application, this would handle sending an email
        System.out.println("Email sent to " + email + " with " + element + token);
    }
}
