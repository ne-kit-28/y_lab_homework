package y_lab.repository.repositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import y_lab.domain.User;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;
    private final String userFile = "testUsers.ser";
    private final String adminFile = "testAdmins.ser";

    @BeforeEach
    void setUp() {
        // Create a new UserRepositoryImpl before each test
        userRepository = new UserRepositoryImpl(userFile, adminFile);
    }

    @Test
    @DisplayName("should assign id and store user")
    void save() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        // Act
        userRepository.save(user);

        // Assert
        assertThat(user.getId()).isEqualTo(0L); // First save should have ID 0
        assertThat(userRepository.getUsers()).hasSize(1);
    }

    @Test
    @DisplayName("should return user when user exists and should return empty when user doesn't exist")
    void findById() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findById(0L);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");

        Optional<User> foundUser_ = userRepository.findById(999L);

        // Assert
        assertThat(foundUser_).isEmpty();
    }

    @Test
    @DisplayName("should return user when user exists and should return empty when user doesn't exist")
    void findByEmail() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");

        Optional<User> foundUser_ = userRepository.findByEmail("notfound@example.com");

        // Assert
        assertThat(foundUser_).isEmpty();
    }

    @Test
    @DisplayName("should remove user when user exists")
    void deleteById() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        // Act
        userRepository.deleteById(0L);

        // Assert
        assertThat(userRepository.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("should modif existing user")
    void update() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated User");

        // Act
        userRepository.update(0L, updatedUser);

        // Assert
        Optional<User> foundUser = userRepository.findById(0L);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("should return true when email exists, should return false when email doesn't exist" +
            ", should return true when email is admin and false if email is not exist")
    void isEmailExist() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        // Act
        boolean exists = userRepository.isEmailExist("test@example.com");

        // Assert
        assertThat(exists).isTrue();

        exists = userRepository.isEmailExist("notfound@example.com");

        // Assert
        assertThat(exists).isFalse();

        ArrayList<String> admins = new ArrayList<>();
        admins.add("admin@example.com");
        userRepository.setAdminEmails(admins);

        // Act
        boolean isAdmin = userRepository.isAdminEmail("admin@example.com");

        // Assert
        assertThat(isAdmin).isTrue();

        isAdmin = userRepository.isAdminEmail("user@example.com");

        // Assert
        assertThat(isAdmin).isFalse();
    }
}
