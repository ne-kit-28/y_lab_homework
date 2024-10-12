package y_lab.usecases;

import y_lab.domain.entities.User;
import y_lab.domain.repositories.UserRepository;

import java.util.Optional;

import static y_lab.usecases.utils.HashFunction.hashPassword;

/**
 * Use case for handling user login functionality.
 * This class validates user credentials and manages login operations.
 */
public class LoginUseCase {
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code LoginUseCase} instance with the specified user repository.
     *
     * @param userRepository the repository for managing users
     */
    public LoginUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Attempts to log in a user with the provided email and password.
     *
     * @param email    the email of the user trying to log in
     * @param password the password of the user trying to log in
     * @return the {@code User} object if login is successful; otherwise, a {@code User} object with id set to -1
     */
    public User login(String email, String password) {

        // Check if the user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            System.out.println("User with this email does not exist!");
            User user_ = new User();
            user_.setId(-1L);
            return user_;
        }

        // Verify password
        if (!user.get().getPasswordHash().equals(hashPassword(password))) {
            System.out.println("Incorrect password!");
            User user_ = new User();
            user_.setId(-1L);
            return user_;
        }

        // Check if the account is blocked
        if (user.get().isBlock()) {
            System.out.println("Your account is blocked!");
            User user_ = new User();
            user_.setId(-1L);
            return user_;
        }

        // Successful login
        System.out.println("Login successful! Welcome, " + user.get().getName());
        return user.get();
    }
}
