package y_lab.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.UserRepository;
import y_lab.util.HashFunction;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return user when login is successful")
    void loginSuccess() {
        String email = "test@example.com";
        String password = "123";
        User user = new User(email, HashFunction.hashPassword(password), "Test User", false, Role.REGULAR);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = loginService.login(email, password);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getName()).isEqualTo("Test User");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should return user with ID -1 when user does not exist")
    void loginUserDoesNotExist() {
        String email = "nonexistent@example.com";
        String password = "123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        User result = loginService.login(email, password);

        assertThat(result.getId()).isEqualTo(-1L);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should return user with ID -1 when password is incorrect")
    void loginIncorrectPassword() {
        String email = "test@example.com";
        String correctPassword = "123";
        String incorrectPassword = "wrongpassword";
        User user = new User(email, HashFunction.hashPassword(correctPassword), "Test User", false, Role.REGULAR);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = loginService.login(email, incorrectPassword);

        assertThat(result.getId()).isEqualTo(-1L);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should return user with ID -1 when account is blocked")
    void loginBlockedAccount() {
        String email = "blocked@example.com";
        String password = "123";
        User user = new User(email, HashFunction.hashPassword(password), "Blocked User", true, Role.REGULAR);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = loginService.login(email, password);

        assertThat(result.getId()).isEqualTo(-1L);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should register user when all conditions are met")
    void registerSuccess() {
        String email = "newuser@example.com";
        String name = "New User";
        String password = "123";
        when(userRepository.isEmailExist(email)).thenReturn(false);
        when(userRepository.isAdminEmail(email)).thenReturn(false);

        loginService.register(name, email, password);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should not register user when email is invalid")
    void registerInvalidEmail() {
        String email = "invalidemail";
        String name = "New User";
        String password = "123";

        loginService.register(name, email, password);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should not register user when email already exists")
    void registerEmailExists() {
        String email = "existing@example.com";
        String name = "Existing User";
        String password = "123";
        when(userRepository.isEmailExist(email)).thenReturn(true);

        loginService.register(name, email, password);

        verify(userRepository, never()).save(any(User.class));
    }
}
