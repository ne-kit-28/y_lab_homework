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
import y_lab.dto.LoginInDto;
import y_lab.dto.LoginResponseDto;
import y_lab.dto.LoginUpDto;
import y_lab.service.LoginService;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешный вход")
    void testSignInSuccess() throws Exception {
        LoginInDto loginInDto = new LoginInDto("user@example.com", "password123");
        LoginResponseDto loginResponseDto = new LoginResponseDto(1L, "user@example.com", "password123", "regular", "");

        when(request.getServletPath()).thenReturn("/api/login/signIn");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(loginInDto))));
        when(loginService.login(any())).thenReturn(loginResponseDto);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        loginController.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).print(1L);
    }

    @Test
    @DisplayName("Невалидные данные для входа")
    void testSignInNotValid() throws Exception {
        LoginInDto loginInDto = new LoginInDto("userexample.com", "password123");

        when(request.getServletPath()).thenReturn("/api/login/signIn");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(loginInDto))));

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        loginController.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Неправильные данные для входа")
    void testSignInFailure() throws Exception {
        LoginInDto loginInDto = new LoginInDto("user@example.com", "wrongpassword");
        LoginResponseDto loginResponseDto = new LoginResponseDto(-1L, "user@example.com", "", "UNAUTHORIZED", "Incorrect password!");

        when(request.getServletPath()).thenReturn("/api/login/signIn");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(loginInDto))));
        when(loginService.login(any())).thenReturn(loginResponseDto);

        loginController.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_CONFLICT, loginResponseDto.message());
    }

    @Test
    @DisplayName("Успешная регистрация")
    void testSignUpSuccess() throws Exception {
        LoginUpDto loginUpDto = new LoginUpDto("user1","user@example.com", "password123");
        LoginResponseDto loginResponseDto = new LoginResponseDto(1L, "user@example.com", "password123", "regular", "Registration successful!");

        when(request.getServletPath()).thenReturn("/api/login/signUp");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(loginUpDto))));
        when(loginService.register(any())).thenReturn(loginResponseDto);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        loginController.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(writer).print(objectMapper.writeValueAsString(loginResponseDto));
    }

    @Test
    @DisplayName("Регистрация с невалидными данными")
    void testSignUpNotValid() throws Exception {
        LoginUpDto loginUpDto = new LoginUpDto("user1","user@example.com", "pas");

        when(request.getServletPath()).thenReturn("/api/login/signUp");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(loginUpDto))));

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        loginController.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Регистрация с существующим email")
    void testSignUpFailure() throws Exception {
        LoginUpDto loginUpDto = new LoginUpDto("user2","user@example.com", "password123");
        LoginResponseDto loginResponseDto = new LoginResponseDto(-1L, "user@example.com", "", "UNAUTHORIZED", "User with this email already exists!");

        when(request.getServletPath()).thenReturn("/api/login/signUp");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(loginUpDto))));
        when(loginService.register(any())).thenReturn(loginResponseDto);

        loginController.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_CONFLICT, loginResponseDto.message());
    }
}
