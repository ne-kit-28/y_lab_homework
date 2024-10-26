package y_lab.service.serviceImpl;

import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.dto.LoginResponseDto;
import y_lab.repository.UserRepository;
import y_lab.service.LoginService;
import y_lab.util.EmailValidator;
import y_lab.util.HashFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private  final Connection connection;

    public LoginServiceImpl(UserRepository userRepository, Connection connection) {
        this.userRepository = userRepository;
        this.connection = connection;
    }

    @Override
    public LoginResponseDto login(User user) {

        try {
            connection.setAutoCommit(false);

            Optional<User> newUser = userRepository.findByEmail(user.getEmail());
            if (newUser.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return new LoginResponseDto(-1L, user.getEmail(), "", "User with this email does not exist!");
            }

            if (!newUser.get().getPasswordHash().equals(user.getPasswordHash())) {
                System.out.println("Incorrect password!");
                return new LoginResponseDto(-1L, user.getEmail(), "", "Incorrect password!");
            }

            if (newUser.get().isBlock()) {
                System.out.println("Your account is blocked!");
                return new LoginResponseDto(-1L, user.getEmail(), "", "Your account is blocked!");
            }

            connection.commit();

            System.out.println("Login successful! Welcome, " + newUser.get().getName());
            return new LoginResponseDto(newUser.get().getId(), user.getEmail(), "", "Successful!");

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
        return new LoginResponseDto(-1L, user.getEmail(), "", "SQL error in loginService");
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString(); // Generate a unique UUID resetToken
    }

    private void sendResetEmail(String email, String token, String element) {
        // In an actual application, this would handle sending an email
        System.out.println("Email sent to " + email + " with " + element + token);
    }

    @Override
    public LoginResponseDto resetPassword(User user) {

        String password = user.getPasswordHash();
        user.setPasswordHash(HashFunction.hashPassword(user.getPasswordHash()));

        try {
            connection.setAutoCommit(false);

            Optional<User> newUser = userRepository.findByEmail(user.getEmail());
            if (newUser.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return new LoginResponseDto(-1L, user.getEmail(), "", "User with this email does not exist!");
            }

            if (!user.getResetToken().equals(newUser.get().getResetToken())) {
                System.out.println("Invalid resetToken!");
                return new LoginResponseDto(-1L, user.getEmail(), "", "Invalid resetToken!");
            }

            newUser.get().setPasswordHash(user.getPasswordHash());
            newUser.get().setResetToken(null);

            userRepository.update(newUser.get().getId(), newUser.get());

            sendResetEmail(newUser.get().getEmail(), password, "new password: ");

            connection.commit();

            System.out.println("Password has been successfully reset!");
            return new LoginResponseDto(newUser.get().getId(), user.getEmail(), password, "Successful!");
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
        return new LoginResponseDto(-1L, user.getEmail(), "", "sql error");
    }

    @Override
    public boolean requestPasswordReset(String email) {

        boolean res = false;

        try {
            connection.setAutoCommit(false);

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                System.out.println("User with this email does not exist!");
                return false;
            }

            String token = generateResetToken();
            user.get().setResetToken(token);

            userRepository.update(user.get().getId(), user.get());

            sendResetEmail(user.get().getEmail(), token, "reset resetToken: ");

            connection.commit();


            System.out.println("Password reset resetToken sent to your email.");
            res = true;
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
        return res;
    }

    @Override
    public LoginResponseDto register(User user) {

        Role role;

        try {
            connection.setAutoCommit(false);

            if (!EmailValidator.isValid(user.getEmail())) {
                System.out.println("Email is incorrect!");
                return new LoginResponseDto(-1L, user.getEmail(), "", "Email is incorrect!");
            }

            if (userRepository.isEmailExist(user.getEmail())) {
                System.out.println("User with this email already exists!");
                return new LoginResponseDto(-1L, user.getEmail(), "", "User with this email already exists!");
            }

            if (userRepository.isAdminEmail(user.getEmail())) {
                role = Role.ADMINISTRATOR;
            } else {
                role = Role.REGULAR;
            }

            User regUser = new User(user.getEmail(), user.getPasswordHash(), user.getName(), false, role);

            userRepository.save(regUser);

            regUser = userRepository.findByEmail(user.getEmail()).get();

            connection.commit();

            System.out.println("Registration successful!");
            return new LoginResponseDto(regUser.getId(), regUser.getEmail(), "", "Successful!");
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
        return new LoginResponseDto(-1L, user.getEmail(), "", "sql error");
    }
}
