package y_lab.out.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import y_lab.domain.entities.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    void save_shouldAssignIdAndStoreUser() {
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
    void findById_shouldReturnUser_whenUserExists() {
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
    }

    @Test
    void findById_shouldReturnEmpty_whenUserDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findById(999L);

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
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
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenUserDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("notfound@example.com");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveUser_whenUserExists() {
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
    void update_shouldModifyExistingUser() {
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
    void isEmailExist_shouldReturnTrue_whenEmailExists() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        // Act
        boolean exists = userRepository.isEmailExist("test@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void isEmailExist_shouldReturnFalse_whenEmailDoesNotExist() {
        // Act
        boolean exists = userRepository.isEmailExist("notfound@example.com");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void isAdminEmail_shouldReturnTrue_whenEmailIsAdmin() {
        // Arrange
        ArrayList<String> admins = new ArrayList<>();
        admins.add("admin@example.com");
        userRepository.setAdminEmails(admins);

        // Act
        boolean isAdmin = userRepository.isAdminEmail("admin@example.com");

        // Assert
        assertThat(isAdmin).isTrue();
    }

    @Test
    void isAdminEmail_shouldReturnFalse_whenEmailIsNotAdmin() {
        // Act
        boolean isAdmin = userRepository.isAdminEmail("user@example.com");

        // Assert
        assertThat(isAdmin).isFalse();
    }

    // Add tests for saveToFile, loadFromFile, saveAdmins, and loadAdminsFromFile if needed

    // Clean up temporary files after tests (optional)
    // @AfterEach
    // void tearDown() {
    //     new File(userFile).delete();
    //     new File(adminFile).delete();
    // }
}
