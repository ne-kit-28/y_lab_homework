package y_lab.service.serviceImpl;

import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.UserRepository;
import y_lab.service.LoginService;
import y_lab.util.EmailValidator;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import static y_lab.util.HashFunction.hashPassword;

public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String email, String password) {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            System.out.println("User with this email does not exist!");
            User user_ = new User();
            user_.setId(-1L);
            return user_;
        }

        if (!user.get().getPasswordHash().equals(hashPassword(password))) {
            System.out.println("Incorrect password!");
            User user_ = new User();
            user_.setId(-1L);
            return user_;
        }

        if (user.get().isBlock()) {
            System.out.println("Your account is blocked!");
            User user_ = new User();
            user_.setId(-1L);
            return user_;
        }

        System.out.println("Login successful! Welcome, " + user.get().getName());
        return user.get();
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString(); // Generate a unique UUID token
    }

    private void sendResetEmail(String email, String token, String element) {
        // In an actual application, this would handle sending an email
        System.out.println("Email sent to " + email + " with " + element + token);
    }

    private void resetPassword(String email, String token, String newPassword) {

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

        sendResetEmail(user.get().getEmail(), newPassword, "new password: ");

        System.out.println("Password has been successfully reset!");
    }

    @Override
    public void requestPasswordReset(String email) {
        Scanner scanner = new Scanner(System.in);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            System.out.println("User with this email does not exist!");
            return;
        }

        String token = generateResetToken();
        user.get().setResetToken(token);

        sendResetEmail(user.get().getEmail(), token, "reset token: ");

        System.out.println("Password reset token sent to your email.");

        System.out.println("Enter your token:");
        String readToken = scanner.nextLine();
        System.out.println("Enter your new password:");
        String password = scanner.nextLine();

        resetPassword(email, readToken, password);
    }

    @Override
    public void register(String name, String email, String password) {

        Role role;

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
    }
}
