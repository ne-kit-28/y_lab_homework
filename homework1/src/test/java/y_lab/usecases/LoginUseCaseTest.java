package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.entities.User;
import y_lab.out.repositories.UserRepositoryImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static y_lab.usecases.utils.HashFunction.hashPassword;

class LoginUseCaseTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_shouldReturnUser_whenCredentialsAreValid() {
        // Arrange
        String email = "user@example.com";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setName("User Name");
        user.setPasswordHash(hashPassword(password));
        user.setBlock(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = loginUseCase.login(email, password);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("User Name");
        verify(userRepository).findByEmail(email);
    }

    @Test
    void login_shouldReturnUserWithIdNegativeOne_whenEmailDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        User result = loginUseCase.login(email, password);

        // Assert
        assertThat(result.getId()).isEqualTo(-1L);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void login_shouldReturnUserWithIdNegativeOne_whenPasswordIsIncorrect() {
        // Arrange
        String email = "user@example.com";
        String wrongPassword = "wrongPassword";
        User user = new User();
        user.setId(1L);
        user.setPasswordHash(hashPassword("correctPassword"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = loginUseCase.login(email, wrongPassword);

        // Assert
        assertThat(result.getId()).isEqualTo(-1L);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void login_shouldReturnUserWithIdNegativeOne_whenAccountIsBlocked() {
        // Arrange
        String email = "user@example.com";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setPasswordHash(hashPassword(password));
        user.setBlock(true); // Account is blocked

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = loginUseCase.login(email, password);

        // Assert
        assertThat(result.getId()).isEqualTo(-1L);
        verify(userRepository).findByEmail(email);
    }
}
