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
import y_lab.controller.api.LoginController;
import y_lab.dto.LoginInDto;
import y_lab.dto.LoginResponseDto;
import y_lab.dto.LoginResetDto;
import y_lab.dto.LoginUpDto;
import y_lab.service.LoginService;
import y_lab.util.HashFunction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private LoginService loginService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("signIn with correct loginInDto")
    public void signInSuccessful() throws Exception {

        LoginInDto loginInDto = new LoginInDto("user@example.com", "1234567");
        LoginResponseDto responseDto = new LoginResponseDto(1L, "user@example.com", "password","ADMINISTRATOR", "Successful!");

        when(loginService.login(any())).thenReturn(responseDto);

        mockMvc.perform(post("/login/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("ADMINISTRATOR"));
    }

    @Test
    @DisplayName("signIn with incorrect password")
    public void signInLoginFails() throws Exception {
        LoginInDto loginInDto = new LoginInDto("user@example.com", "wrongpassword");
        LoginResponseDto responseDto = new LoginResponseDto(-1L, "user@example.com", "","UNAUTHORIZED","Incorrect password!");

        when(loginService.login(any())).thenReturn(responseDto);

        mockMvc.perform(post("/login/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(-1L));
    }

    @Test
    @DisplayName("SignUp with correct signUpDto")
    public void signUpSuccessful() throws Exception {

        LoginUpDto loginUpDto = new LoginUpDto("User","newuser@example.com", "password");
        LoginResponseDto responseDto = new LoginResponseDto(2L, "newuser@example.com", HashFunction.hashPassword("password"),"UNAUTHORIZED","Incorrect password!");

        when(loginService.register(any())).thenReturn(responseDto);

        mockMvc.perform(post("/login/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUpDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.role").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("signUp with incorrect data")
    public void signUpFails() throws Exception {

        LoginUpDto loginUpDto = new LoginUpDto("user","existinguser@example.com", "password");
        LoginResponseDto responseDto = new LoginResponseDto(-1L, "existinguser@example.com","","UNAUTHORIZED", "error");

        when(loginService.register(any())).thenReturn(responseDto);

        mockMvc.perform(post("/login/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUpDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(-1L));
    }

    @Test
    @DisplayName("Request reset password")
    public void requestOk() throws Exception {

        String email = "user@example.com";
        when(loginService.requestPasswordReset(email)).thenReturn(true);

        mockMvc.perform(post("/login/requestReset/{email}", email))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("request with invalid email")
    public void requestConflict() throws Exception {

        String email = "invalid@example.com";
        when(loginService.requestPasswordReset(email)).thenReturn(false);

        mockMvc.perform(post("/login/requestReset/{email}", email))
                .andExpect(status().isConflict())
                .andExpect(header().string("Error-Message", "Error with email"));
    }

    @Test
    @DisplayName("Reset with correct data")
    public void resetSuccessful() throws Exception {

        LoginResetDto resetDto = new LoginResetDto("user@example.com", "newpassword", "token123345");
        LoginResponseDto responseDto = new LoginResponseDto(1L, "user@example.com", "","UNAUTHORIZED","ok");

        when(loginService.resetPassword(any())).thenReturn(responseDto);

        mockMvc.perform(post("/login/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("reset fails")
    public void resetConflict() throws Exception {

        LoginResetDto resetDto = new LoginResetDto("user@example.com", "wrongpassword", "token123456");
        LoginResponseDto responseDto = new LoginResponseDto(-1L, "user@example.com", "","UNAUTHORIZED","Incorrect password!");

        when(loginService.resetPassword(any())).thenReturn(responseDto);

        mockMvc.perform(post("/login/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(-1L));
    }
}
