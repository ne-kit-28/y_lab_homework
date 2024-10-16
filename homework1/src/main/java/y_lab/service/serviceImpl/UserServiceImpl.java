package y_lab.service.serviceImpl;

import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.UserService;
import y_lab.util.EmailValidator;
import y_lab.util.HashFunction;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;
    private final HabitRepositoryImpl habitRepository;
    private final ProgressRepositoryImpl progressRepository;

    public UserServiceImpl(UserRepositoryImpl userRepository, HabitRepositoryImpl habitRepository, ProgressRepositoryImpl progressRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public void editUser(Long id, String newName, String newEmail, String newPassword) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            System.out.println("User with this id does not exist!");
            return;
        }

        // Check for unique new email
        if (newEmail != null && !newEmail.isEmpty() && userRepository.findByEmail(newEmail).isPresent()) {
            System.out.println("Email already in use by another account!");
            return;
        }

        if (newEmail != null && !newEmail.isEmpty() && !EmailValidator.isValid(newEmail)) {
            System.out.println("Email is incorrect!");
            return;
        }

        if (newName != null && !newName.isEmpty())
            user.get().setName(newName);
        if (newEmail != null && !newEmail.isEmpty())
            user.get().setEmail(newEmail);
        if (newPassword != null && !newPassword.isEmpty())
            user.get().setPasswordHash(HashFunction.hashPassword(newPassword));

        System.out.println("Profile updated successfully!");
    }

    @Override
    public void blockUser(Long id, boolean block) {
        // Check for null
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        // Find user by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found."));

        // Block the user
        user.setBlock(block);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        habitRepository.deleteAllByUserId(id);
        progressRepository.deleteAllByUserId(id);
        System.out.println("User and all habits were deleted!");
    }

    @Override
    public Optional<User> getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
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
}
