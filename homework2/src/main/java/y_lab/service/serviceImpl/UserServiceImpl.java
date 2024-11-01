package y_lab.service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.HabitRepository;
import y_lab.repository.ProgressRepository;
import y_lab.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final ProgressRepository progressRepository;
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


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

    //Проверка на возможность обновить пользователя
    private boolean checkUser(User user, long id) throws SQLException {

        Optional<User> myUser = userRepository.findById(id);
        if (myUser.isEmpty()) {
            logger.info("User with this id does not exist!");
            return false;
        }
        // Check for unique new email
        if (user.getEmail() != null && !user.getEmail().isEmpty() && userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.info("Email already in use by another account!");
            return false;
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !EmailValidator.isValid(user.getEmail())) {
            logger.info("Email is incorrect!");
            return false;
        }
        return true;
    }

    @Override
    public boolean editUser(Long id, User user) {

        boolean edit = false;

        try {
            connection.setAutoCommit(false);

            Optional<User> myUser = userRepository.findById(id);

            if (!checkUser(user, id)) //валидация
                return false;

            myUser.get().setName(user.getName());
            myUser.get().setEmail(user.getEmail());
            myUser.get().setPasswordHash(user.getPasswordHash());

            userRepository.update(myUser.get().getId(), myUser.get());

            connection.commit();

            logger.info("Profile updated successfully!");
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

            logger.info("Profile block is " + user.isBlock());
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

            logger.info("User and all habits were deleted!");
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
                logger.info("No users");
            } else {
                for (User user : users) {
                    logger.info("Email: " + user.getEmail());
                    logger.info("Name: " + user.getName());
                    logger.info("Is blocked: " + user.isBlock() + '\n');
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
