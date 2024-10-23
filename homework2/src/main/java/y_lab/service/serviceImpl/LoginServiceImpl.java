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
    public User login(String email, String password) {

        User user_ = new User();
        user_.setId(-1L);

        try {
            connection.setAutoCommit(false);

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return user_;
            }

            if (!user.get().getPasswordHash().equals(hashPassword(password))) {
                System.out.println("Incorrect password!");
                return user_;
            }

            if (user.get().isBlock()) {
                System.out.println("Your account is blocked!");
                return user_;
            }

            connection.commit();

            System.out.println("Login successful! Welcome, " + user.get().getName());
            return user.get();

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
        return UUID.randomUUID().toString(); // Generate a unique UUID token
    }

    private void sendResetEmail(String email, String token, String element) {
        // In an actual application, this would handle sending an email
        System.out.println("Email sent to " + email + " with " + element + token);
    }

    @Override
    public void resetPassword(String email, String token, String newPassword) {

        try {
            connection.setAutoCommit(false);

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return;
            }

            if (!token.equals(user.get().getResetToken())) {
                System.out.println("Invalid token!");
                return;
            }

            user.get().setPasswordHash(hashPassword(newPassword));
            user.get().setResetToken(null);

            userRepository.update(user.get().getId(), user.get());

            sendResetEmail(user.get().getEmail(), newPassword, "new password: ");

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

            sendResetEmail(user.get().getEmail(), token, "reset token: ");

            System.out.println("Password reset token sent to your email.");

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
    public void register(String name, String email, String password) {

        Role role;

        try {
            connection.setAutoCommit(false);

            if (!EmailValidator.isValid(email)) {
                System.out.println("Email is incorrect!");
                return;
            }

            if (userRepository.isEmailExist(email)) {
                System.out.println("User with this email already exists!");
                return;
            }

            if (userRepository.isAdminEmail(email)) {
                role = Role.ADMINISTRATOR;
            } else {
                role = Role.REGULAR;
            }

            User user = new User(email, hashPassword(password), name, false, role);

            userRepository.save(user);

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
