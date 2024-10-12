package y_lab.usecases;

import y_lab.domain.entities.Role;
import y_lab.domain.entities.User;
import y_lab.domain.repositories.UserRepository;
import y_lab.service.EmailValidatorService;

import static y_lab.usecases.utils.HashFunction.hashPassword;

/**
 * Use case for handling user registration.
 * This class is responsible for registering new users by validating input data,
 * checking for existing users, and saving the new user to the repository.
 */
public class RegistrationUseCase {
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code RegistrationUseCase} instance with the specified user repository.
     *
     * @param userRepository the repository used for managing user data
     */
    public RegistrationUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user with the given name, email, and password.
     * This method validates the email, checks if the email already exists,
     * assigns the appropriate role based on the email, and saves the user.
     *
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password of the user
     */
    public void register(String name, String email, String password) {

        Role role;

        // Validate email format
        if (!EmailValidatorService.isValid(email)) {
            System.out.println("Email is incorrect!");
            return;
        }

        // Check if the email already exists
        if (userRepository.isEmailExist(email)) {
            System.out.println("User with this email already exists!");
            return;
        }

        // Assign role based on the email
        if (userRepository.isAdminEmail(email)) {
            role = Role.ADMINISTRATOR;
        } else {
            role = Role.REGULAR;
        }

        // Create a new user
        User user = new User(email, hashPassword(password), name, false, role);

        // Save the user to the repository
        userRepository.save(user);

        System.out.println("Registration successful!");
    }
}
