package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import y_lab.domain.entities.User;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;
import y_lab.out.repositories.UserRepositoryImpl;
import y_lab.service.EmailValidatorService;
import y_lab.usecases.utils.HashFunction;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EditUserUseCaseTest {

    private UserRepositoryImpl userRepository;
    private HabitRepositoryImpl habitRepository;
    private ProgressRepositoryImpl progressRepository;
    private EditUserUseCase editUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryImpl.class);
        habitRepository = mock(HabitRepositoryImpl.class);
        progressRepository = mock(ProgressRepositoryImpl.class);
        editUserUseCase = new EditUserUseCase(userRepository, habitRepository, progressRepository);
    }

    @Test
    void editUser_shouldUpdateUserDetails_whenValidDataProvided() {
        // Arrange
        Long userId = 1L;
        String newName = "New Name";
        String newEmail = "new@example.com";
        String newPassword = "newPassword";

        // Create an existing user with the given ID
        User existingUser = new User();
        existingUser.setId(userId);

        // Stubbing methods on the mock repository
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());

        // Mocking the static method for email validation
        try (MockedStatic<EmailValidatorService> mockedStatic = Mockito.mockStatic(EmailValidatorService.class)) {
            mockedStatic.when(() -> EmailValidatorService.isValid(newEmail)).thenReturn(true);

            // Act
            editUserUseCase.editUser(userId, newName, newEmail, newPassword);

            // Assert
            assertThat(existingUser.getName()).isEqualTo(newName);
            assertThat(existingUser.getEmail()).isEqualTo(newEmail);
            assertThat(existingUser.getPasswordHash()).isEqualTo(HashFunction.hashPassword(newPassword));

            // Verify that the necessary repository methods were called
            verify(userRepository).findById(userId);
            verify(userRepository).findByEmail(newEmail);
        }
    }

    @Test
    void editUser_shouldNotUpdate_whenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        editUserUseCase.editUser(userId, "New Name", "new@example.com", "newPassword");

        // Assert
        verify(userRepository).findById(userId);
    }

    @Test
    void editUser_shouldNotUpdate_whenEmailAlreadyInUse() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(new User()));

        // Mock static method
        try (MockedStatic<EmailValidatorService> mockedStatic = Mockito.mockStatic(EmailValidatorService.class)) {
            mockedStatic.when(() -> EmailValidatorService.isValid("new@example.com")).thenReturn(true);

            // Act
            editUserUseCase.editUser(userId, "New Name", "new@example.com", "newPassword");

            // Assert
            assertThat(existingUser.getName()).isNotEqualTo("New Name");
            verify(userRepository).findById(userId);
            verify(userRepository).findByEmail("new@example.com");
        }
    }

    @Test
    void deleteUser_shouldDeleteUserAndAssociatedRecords() {
        // Arrange
        Long userId = 1L;

        // Act
        editUserUseCase.deleteUser(userId);

        // Assert
        verify(userRepository).deleteById(userId);
        verify(habitRepository).deleteAllByUserId(userId);
        verify(progressRepository).deleteAllByUserId(userId);
    }

    @Test
    void blockUser_shouldBlockUser_whenValidIdProvided() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        editUserUseCase.blockUser(userId, true);

        // Assert
        assertThat(user.isBlock()).isTrue();
        verify(userRepository).findById(userId);
    }

    @Test
    void blockUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () -> {
            editUserUseCase.blockUser(userId, true);
        });
        verify(userRepository).findById(userId);
    }

    @Test
    void blockUser_shouldThrowException_whenIdIsNull() {
        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            editUserUseCase.blockUser(null, true);
        });
    }
}
