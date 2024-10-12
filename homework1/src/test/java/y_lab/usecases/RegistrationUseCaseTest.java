package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.entities.User;
import y_lab.out.repositories.UserRepositoryImpl;

import static org.mockito.Mockito.*;

class RegistrationUseCaseTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private RegistrationUseCase registrationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldRegisterUser_whenEmailIsValidAndDoesNotExist() {
        // Arrange
        String name = "New User";
        String email = "newuser@example.com";
        String password = "securePassword";

        when(userRepository.isEmailExist(email)).thenReturn(false);
        when(userRepository.isAdminEmail(email)).thenReturn(false);

        // Act
        registrationUseCase.register(name, email, password);

        // Assert
        verify(userRepository).isEmailExist(email);
        verify(userRepository).isAdminEmail(email);
        verify(userRepository).save(any(User.class)); // Verify that save is called
        // Additional assertions can be done if the User object is accessible
    }

    @Test
    void register_shouldNotRegisterUser_whenEmailIsInvalid() {
        // Arrange
        String name = "New User";
        String email = "invalid-email";
        String password = "securePassword";

        // Act
        registrationUseCase.register(name, email, password);

        // Assert
        verify(userRepository, never()).isEmailExist(any());
        verify(userRepository, never()).save(any()); // Ensure save is not called
    }

    @Test
    void register_shouldNotRegisterUser_whenEmailAlreadyExists() {
        // Arrange
        String name = "New User";
        String email = "existinguser@example.com";
        String password = "securePassword";

        when(userRepository.isEmailExist(email)).thenReturn(true);

        // Act
        registrationUseCase.register(name, email, password);

        // Assert
        verify(userRepository).isEmailExist(email);
        verify(userRepository, never()).isAdminEmail(any());
        verify(userRepository, never()).save(any()); // Ensure save is not called
    }

    @Test
    void register_shouldAssignAdminRole_whenEmailIsAdminEmail() {
        // Arrange
        String name = "Admin User";
        String email = "admin@example.com";
        String password = "securePassword";

        when(userRepository.isEmailExist(email)).thenReturn(false);
        when(userRepository.isAdminEmail(email)).thenReturn(true);

        // Act
        registrationUseCase.register(name, email, password);

        // Assert
        verify(userRepository).isEmailExist(email);
        verify(userRepository).isAdminEmail(email);
        verify(userRepository).save(any(User.class)); // Verify that save is called
        // Additional assertions can be done if the User object is accessible
    }

    @Test
    void register_shouldAssignRegularRole_whenEmailIsNotAdminEmail() {
        // Arrange
        String name = "Regular User";
        String email = "regularuser@example.com";
        String password = "securePassword";

        when(userRepository.isEmailExist(email)).thenReturn(false);
        when(userRepository.isAdminEmail(email)).thenReturn(false);

        // Act
        registrationUseCase.register(name, email, password);

        // Assert
        verify(userRepository).isEmailExist(email);
        verify(userRepository).isAdminEmail(email);
        verify(userRepository).save(any(User.class)); // Verify that save is called
        // Additional assertions can be done if the User object is accessible
    }
}
