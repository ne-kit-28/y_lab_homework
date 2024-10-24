package y_lab.service.serviceImpl;

import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.UserRepository;
import y_lab.service.LoginService;
import y_lab.util.EmailValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static y_lab.util.HashFunction.hashPassword;

public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private  final Connection connection;

    public LoginServiceImpl(UserRepository userRepository, Connection connection) {
        this.userRepository = userRepository;
        this.connection = connection;
    }

    @Override
    public User login(User user) {

        User user_ = new User();
        user_.setId(-1L);

        try {
            connection.setAutoCommit(false);

            Optional<User> newUser = userRepository.findByEmail(user.getEmail());
            if (newUser.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return user_;
            }

            if (!newUser.get().getPasswordHash().equals(user.getPasswordHash())) {
                System.out.println("Incorrect password!");
                return user_;
            }

            if (newUser.get().isBlock()) {
                System.out.println("Your account is blocked!");
                return user_;
            }

            connection.commit();

            System.out.println("Login successful! Welcome, " + newUser.get().getName());
            return newUser.get();

        } catch (SQLException e) {
            try {
                connection.rollback();
                System.out.println("SQL error in loginService");
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
        return user_;
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString(); // Generate a unique UUID resetToken
    }

    private void sendResetEmail(String email, String token, String element) {
        // In an actual application, this would handle sending an email
        System.out.println("Email sent to " + email + " with " + element + token);
    }

    @Override
    public void resetPassword(User user) {

        try {
            connection.setAutoCommit(false);

            Optional<User> newUser = userRepository.findByEmail(user.getEmail());
            if (newUser.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return;
            }

            if (!user.getResetToken().equals(newUser.get().getResetToken())) {
                System.out.println("Invalid resetToken!");
                return;
            }

            newUser.get().setPasswordHash(user.getPasswordHash());
            newUser.get().setResetToken(null);

            userRepository.update(newUser.get().getId(), newUser.get());

            sendResetEmail(newUser.get().getEmail(), user.getPasswordHash(), "new passwordHash: ");

            System.out.println("Password has been successfully reset!");

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
    public void requestPasswordReset(String email) {

        try {
            connection.setAutoCommit(false);

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return;
            }

            String token = generateResetToken();
            user.get().setResetToken(token);

            userRepository.update(user.get().getId(), user.get());

            sendResetEmail(user.get().getEmail(), token, "reset resetToken: ");

            System.out.println("Password reset resetToken sent to your email.");

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
    public void register(User user) {

        Role role;

        try {
            connection.setAutoCommit(false);

            if (!EmailValidator.isValid(user.getEmail())) {
                System.out.println("Email is incorrect!");
                return;
            }

            if (userRepository.isEmailExist(user.getEmail())) {
                System.out.println("User with this email already exists!");
                return;
            }

            if (userRepository.isAdminEmail(user.getEmail())) {
                role = Role.ADMINISTRATOR;
            } else {
                role = Role.REGULAR;
            }

            User regUser = new User(user.getEmail(), user.getPasswordHash(), user.getName(), false, role);

            userRepository.save(regUser);

            System.out.println("Registration successful!");

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
}
