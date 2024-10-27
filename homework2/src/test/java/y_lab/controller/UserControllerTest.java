package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.dto.UserRequestDto;
import y_lab.mapper.UserMapper;
import y_lab.mapper.UserMapperImpl;
import y_lab.service.UserService;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserMapper userMapper = new UserMapperImpl();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешное полуение всех пользователей")
    void testGetAllUsersSuccess() throws Exception {
        when(request.getServletPath()).thenReturn("/api/user/all");
        ArrayList<User> users = new ArrayList<>();
        users.add((new User(1L, "email@yandex.ru", "smth_hash", "User", false, Role.REGULAR, null)));
        when(userService.getUsers()).thenReturn(users);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        userController.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write(any(String.class));
    }

    @Test
    @DisplayName("Получение пользователя по почте")
    void testGetUserByEmailSuccess() throws Exception {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(request.getParameter("email")).thenReturn(email);
        when(userService.getUser(email)).thenReturn(Optional.of(user));

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        userController.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write(any(String.class));
    }

    @Test
    @DisplayName("Получение пользователя по неверной почте")
    void testGetUserByEmailNotFound() throws Exception {
        String email = "nonexistent@example.com";

        when(request.getParameter("email")).thenReturn(email);
        when(userService.getUser(email)).thenReturn(Optional.empty());

        userController.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
    }

    @Test
    @DisplayName("Успешное изменение пользователя")
    void testEditUserSuccess() throws Exception {
        Long userId = 1L;
        UserRequestDto userRequestDto = new UserRequestDto("new@example.com", "newName", "123456");

        when(request.getParameter("userId")).thenReturn(String.valueOf(userId));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(userRequestDto))));
        when(userService.editUser(eq(userId), any(User.class))).thenReturn(true);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        userController.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).print(true);
    }

    @Test
    @DisplayName("Неуспешное изменение пользователя")
    void testEditUserFailure() throws Exception {
        Long userId = 1L;
        UserRequestDto userRequestDto = new UserRequestDto("new@example.com", "newName", "123456");

        when(request.getParameter("userId")).thenReturn(String.valueOf(userId));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(userRequestDto))));
        when(userService.editUser(eq(userId), any(User.class))).thenReturn(false);

        userController.doPut(request, response);

        verify(response).sendError(HttpServletResponse.SC_CONFLICT, "User was not updated");
    }

    @Test
    @DisplayName("Тест на успешное удаление пользователя")
    void testDeleteUserSuccess() throws Exception {
        Long userId = 1L;

        when(request.getParameter("userId")).thenReturn(String.valueOf(userId));
        when(userService.deleteUser(userId)).thenReturn(true);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        userController.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).print(true);
    }

    @Test
    @DisplayName("Тест на неуспешное удаление пользователя")
    void testDeleteUserFailure() throws Exception {
        Long userId = 1L;

        when(request.getParameter("userId")).thenReturn(String.valueOf(userId));
        when(userService.deleteUser(userId)).thenReturn(false);

        userController.doDelete(request, response);

        verify(response).sendError(HttpServletResponse.SC_CONFLICT, "Nothing deleted");
    }
}
