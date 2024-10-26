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
    public boolean editUser(Long id, User user) {

        boolean edit = false;

        try {
            connection.setAutoCommit(false);

            Optional<User> myUser = userRepository.findById(id);

            if (myUser.isEmpty()) {
                System.out.println("User with this id does not exist!");
                return false;
            }

            // Check for unique new email
            if (user.getEmail() != null && !user.getEmail().isEmpty() && userRepository.findByEmail(user.getEmail()).isPresent()) {
                System.out.println("Email already in use by another account!");
                return false;
            }

            if (user.getEmail() != null && !user.getEmail().isEmpty() && !EmailValidator.isValid(user.getEmail())) {
                System.out.println("Email is incorrect!");
                return false;
            }

            if (user.getName() != null && !user.getName().isEmpty())
                myUser.get().setName(user.getName());
            if (user.getEmail() != null && !user.getEmail().isEmpty())
                myUser.get().setEmail(user.getEmail());
            if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty())
                myUser.get().setPasswordHash(user.getPasswordHash());

            userRepository.update(myUser.get().getId(), myUser.get());

            connection.commit();

            System.out.println("Profile updated successfully!");
            edit = true;
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
        return edit;
    }

    @Override
    public boolean blockUser(Long id, boolean block) {

        boolean edit = false;

        try {
            connection.setAutoCommit(false);

            if (id == null) {
                return false;
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found."));

            user.setBlock(block);

            userRepository.update(user.getId(), user);

            connection.commit();

            System.out.println("Profile block is " + user.isBlock());
            edit = true;
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
        return edit;
    }

    @Override
    public boolean deleteUser(Long id) {

        boolean edit = false;

        try {
            connection.setAutoCommit(false);

            userRepository.deleteById(id);
            habitRepository.deleteAllByUserId(id);
            progressRepository.deleteAllByUserId(id);

            connection.commit();

            System.out.println("User and all habits were deleted!");
            edit = true;
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
        return edit;
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
