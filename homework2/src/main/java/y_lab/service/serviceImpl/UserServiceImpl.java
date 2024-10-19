package y_lab.service.serviceImpl;

import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.UserService;
import y_lab.util.EmailValidator;
import y_lab.util.HashFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;
    private final HabitRepositoryImpl habitRepository;
    private final ProgressRepositoryImpl progressRepository;
    private final Connection connection;

    public UserServiceImpl(
            UserRepositoryImpl userRepository
            , HabitRepositoryImpl habitRepository
            , ProgressRepositoryImpl progressRepository
            , Connection connection) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
        this.progressRepository = progressRepository;
        this.connection = connection;
    }

    @Override
    public void editUser(Long id, String newName, String newEmail, String newPassword) {

        try {
            connection.setAutoCommit(false);

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

            userRepository.update(user.get().getId(), user.get());
            System.out.println("Profile updated successfully!");

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void blockUser(Long id, boolean block) {

        try {
            connection.setAutoCommit(false);

            if (id == null) {
                throw new IllegalArgumentException("User ID cannot be null.");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found."));

            user.setBlock(block);

            userRepository.update(user.getId(), user);
            System.out.println("Profile block is " + user.isBlock());

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void deleteUser(Long id) {

        try {
            connection.setAutoCommit(false);

            userRepository.deleteById(id);
            habitRepository.deleteAllByUserId(id);
            progressRepository.deleteAllByUserId(id);
            System.out.println("User and all habits were deleted!");

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Optional<User> getUser(String email) {

        Optional<User> user = Optional.empty();

        try {
            connection.setAutoCommit(false);

            user = userRepository.findByEmail(email);

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public ArrayList<User> getUsers() {

        ArrayList<User> users = new ArrayList<>();

        try {
            connection.setAutoCommit(false);

            users = new ArrayList<>(userRepository.getAll().stream()
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

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return users;
    }
}
