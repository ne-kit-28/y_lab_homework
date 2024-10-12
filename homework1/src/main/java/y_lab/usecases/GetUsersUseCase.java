package y_lab.usecases;

import y_lab.domain.entities.Role;
import y_lab.domain.entities.User;
import y_lab.domain.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Use case for retrieving users from the user repository.
 * This class provides functionality to retrieve all regular users and find a user by their email.
 */
public class GetUsersUseCase {
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code GetUsersUseCase} instance with the specified user repository.
     *
     * @param userRepository the repository for managing users
     */
    public GetUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves and displays all regular users from the repository.
     *
     * @return a list of regular users
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>(userRepository.getAll().stream()
                .filter(user -> user.getRole().equals(Role.REGULAR))
                .toList());

        if (users.isEmpty()) {
            System.out.println("No users");
        } else {
            for (User user : users) {
                System.out.println("Email: " + user.getEmail());
                System.out.println("Name: " + user.getName());
                System.out.println("Is blocked: " + user.isBlock());
                System.out.println();
            }
        }

        return users;
    }

    /**
     * Retrieves a user by their email from the repository.
     *
     * @param email the email of the user to retrieve
     * @return an {@code Optional<User>} containing the user if found, or an empty Optional if not found
     */
    public Optional<User> getUser(String email) {
        return userRepository.findByEmail(email);
    }
}
