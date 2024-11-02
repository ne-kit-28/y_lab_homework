package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import y_lab.controller.api.UserController;
import y_lab.domain.User;
import y_lab.dto.UserRequestDto;
import y_lab.dto.UserResponseDto;
import y_lab.mapper.UserMapper;
import y_lab.service.UserService;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Get user with valid data")
    void getUser() throws Exception {

        User user = new User();
        user.setEmail("test@example.com");

        UserResponseDto userResponseDto = new UserResponseDto(1l, "test@example.com", "user", false);

        when(userService.getUser(anyString())).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponseDto);

        // Act & Assert
        mockMvc.perform(get("/user/1/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Successfully update")
    void updateUser() throws Exception {

        UserRequestDto userRequestDto = new UserRequestDto("test@example.com", "user", "1234567");

        when(userService.editUser(anyLong(), any())).thenReturn(true);

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deleting user")
    void deleteUser() throws Exception {

        when(userService.deleteUser(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk());
    }
}
