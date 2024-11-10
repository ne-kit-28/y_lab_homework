package y_lab.service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import y_lab.audit_logging_spring_boot_starter.annotation.Auditable;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.dto.LoginResponseDto;
import y_lab.repository.UserRepository;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.LoginService;
import y_lab.util.EmailValidator;
import y_lab.util.HashFunction;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    public LoginServiceImpl(UserRepositoryImpl userRepository) throws SQLException {
        this.userRepository = userRepository;
    }

    @Override
    @Auditable
    public LoginResponseDto login(User user) {

        try {
            Optional<User> newUser = userRepository.findByEmail(user.getEmail());
            if (newUser.isEmpty()) {
                logger.info("User with this email does not exist!");
                return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(),"User with this email does not exist!");
            }

            if (!newUser.get().getPasswordHash().equals(user.getPasswordHash())) {
                logger.info("Incorrect password!");
                return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "Incorrect password!");
            }

            if (newUser.get().isBlock()) {
                logger.info("Your account is blocked!");
                return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "Your account is blocked!");
            }

            logger.info("Login successful! Welcome, " + newUser.get().getName());
            return new LoginResponseDto(newUser.get().getId(), user.getEmail(), "", newUser.get().getRole().getValue(), "Successful!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "SQL error in loginService");
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString(); // Generate a unique UUID resetToken
    }

    private void sendResetEmail(String email, String token, String element) {
        System.out.println("Email sent to " + email + " with " + element + token);
    }

    @Override
    @Auditable
    public LoginResponseDto resetPassword(User user) {

        String password = user.getPasswordHash();
        user.setPasswordHash(HashFunction.hashPassword(user.getPasswordHash()));

        try {
            Optional<User> newUser = userRepository.findByEmail(user.getEmail());
            if (newUser.isEmpty()) {
                logger.info("User with this email does not exist!");
                return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(),"User with this email does not exist!");
            }

            if (!user.getResetToken().equals(newUser.get().getResetToken())) {
                logger.info("Invalid resetToken!");
                return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "Invalid resetToken!");
            }

            newUser.get().setPasswordHash(user.getPasswordHash());
            newUser.get().setResetToken(null);

            userRepository.update(newUser.get().getId(), newUser.get());

            sendResetEmail(newUser.get().getEmail(), password, "new password: ");

            logger.info("Password has been successfully reset!");
            return new LoginResponseDto(newUser.get().getId(), user.getEmail(), password, newUser.get().getRole().getValue(),"Successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "sql error");
    }

    @Override
    @Auditable
    public boolean requestPasswordReset(String email) {

        boolean res = false;

        try {
           Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                logger.info("User with this email does not exist!");
                return false;
            }

            String token = generateResetToken();
            user.get().setResetToken(token);

            userRepository.update(user.get().getId(), user.get());

            sendResetEmail(user.get().getEmail(), token, "reset resetToken: ");

            logger.info("Password reset resetToken sent to your email.");
            res = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    @Auditable
    public LoginResponseDto register(User user) {

        Role role;

        try {
            if (!EmailValidator.isValid(user.getEmail())) {
                logger.info("Email is incorrect!");
                return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "Email is incorrect!");
            }

            if (userRepository.isEmailExist(user.getEmail())) {
                logger.info("User with this email already exists!");
                return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "User with this email already exists!");
            }

            if (userRepository.isAdminEmail(user.getEmail())) {
                role = Role.ADMINISTRATOR;
            } else {
                role = Role.REGULAR;
            }

            User regUser = new User(user.getEmail(), user.getPasswordHash(), user.getName(), false, role);

            userRepository.save(regUser);

            regUser = userRepository.findByEmail(user.getEmail()).get();

            logger.info("Registration successful!");
            return new LoginResponseDto(regUser.getId(), regUser.getEmail(), "", regUser.getRole().getValue(), "Successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LoginResponseDto(-1L, user.getEmail(), "", Role.UNAUTHORIZED.getValue(), "sql error");
    }
}
