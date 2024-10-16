package y_lab.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.util.HashFunction;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private HabitRepositoryImpl habitRepository;

    @Mock
    private ProgressRepositoryImpl progressRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(Role.REGULAR);
    }

    @Test
    @DisplayName("Should update user profile when valid data is provided")
    void editUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        userService.editUser(1L, "New Name", "new@example.com", "newPassword");

        // Assert
        assertThat(testUser.getName()).isEqualTo("New Name");
        assertThat(testUser.getEmail()).isEqualTo("new@example.com");
        assertThat(testUser.getPasswordHash()).isEqualTo(HashFunction.hashPassword("newPassword"));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user is not found for blocking")
    void blockUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> userService.blockUser(1L, true));

        assertThat(exception.getMessage()).isEqualTo("User with ID 1 not found.");
    }

    @Test
    @DisplayName("Should delete user, habits, and progress when deleteUser is called")
    void deleteUser() {
        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
        verify(habitRepository, times(1)).deleteAllByUserId(1L);
        verify(progressRepository, times(1)).deleteAllByUserId(1L);
    }

    @Test
    @DisplayName("Should return optional user when email is found")
    void getUser() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUser("test@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return a list of regular users")
    void getUsers() {
        // Arrange
        ArrayList<User> users = new ArrayList<>();
        users.add(testUser);
        when(userRepository.getAll()).thenReturn(users);

        // Act
        ArrayList<User> result = userService.getUsers();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(Role.REGULAR);
        verify(userRepository, times(1)).getAll();
    }
}
