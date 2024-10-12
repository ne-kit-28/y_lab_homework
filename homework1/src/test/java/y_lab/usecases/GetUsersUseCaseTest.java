package y_lab.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.entities.Role;
import y_lab.domain.entities.User;
import y_lab.out.repositories.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetUsersUseCaseTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private GetUsersUseCase getUsersUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_shouldReturnRegularUsers_whenUsersExist() {
        // Arrange
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setName("User One");
        user1.setBlock(false);
        user1.setRole(Role.REGULAR);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setName("User Two");
        user2.setBlock(false);
        user2.setRole(Role.REGULAR);

        ArrayList<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(userRepository.getAll()).thenReturn(users);

        // Act
        ArrayList<User> result = getUsersUseCase.getUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(user1, user2);
        verify(userRepository).getAll();
    }

    @Test
    void getUsers_shouldReturnEmptyList_whenNoRegularUsers() {
        // Arrange
        ArrayList<User> users = new ArrayList<>();
        when(userRepository.getAll()).thenReturn(users);

        // Act
        ArrayList<User> result = getUsersUseCase.getUsers();

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).getAll();
    }

    @Test
    void getUser_shouldReturnUser_whenEmailExists() {
        // Arrange
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = getUsersUseCase.getUser(email);

        // Assert
        assertThat(result).isPresent().contains(user);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUser_shouldReturnEmptyOptional_whenEmailDoesNotExist() {
        // Arrange
        String email = "user@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = getUsersUseCase.getUser(email);

        // Assert
        assertThat(result).isNotPresent();
        verify(userRepository).findByEmail(email);
    }
}
