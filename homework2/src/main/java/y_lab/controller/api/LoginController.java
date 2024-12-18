package y_lab.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import y_lab.dto.LoginInDto;
import y_lab.dto.LoginResetDto;
import y_lab.dto.LoginResponseDto;
import y_lab.dto.LoginUpDto;
import y_lab.mapper.LoginMapper;
import y_lab.mapper.LoginMapperImpl;
import y_lab.service.LoginService;
import y_lab.util.JwtUtil;

@RestController
@RequestMapping(value = "/login")
@Tag(name = "Login", description = "Operations related to user login and registration")
public class LoginController {

    private final LoginMapper loginMapper;
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
        loginMapper = new LoginMapperImpl();
    }

    @Operation(summary = "User sign in", description = "Authenticate user and return JWT token")
    @PostMapping(value = "/signIn")
    public ResponseEntity<LoginResponseDto> signIn(@RequestBody @Valid LoginInDto loginInDto) {
        LoginResponseDto loginResponseDto = loginService.login(loginMapper.loginInDtoToUser(loginInDto));
        if (loginResponseDto.id() != -1L)
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Authorization", "Bearer " + JwtUtil.generateToken(loginResponseDto.id(), loginResponseDto.role()))
                    .body(loginResponseDto);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponseDto);
    }

    @Operation(summary = "User sign up", description = "Register a new user")
    @PostMapping(value = "/signUp")
    public ResponseEntity<LoginResponseDto> signUp(@RequestBody @Valid LoginUpDto loginUpDto) {
        LoginResponseDto loginResponseDto = loginService.register(loginMapper.loginUpDtoToUser(loginUpDto));
        if (loginResponseDto.id() != -1L)
            return ResponseEntity.ok(loginResponseDto);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponseDto);
    }

    @Operation(summary = "Request password reset", description = "Send a password reset request to the user")
    @PostMapping(value = "/requestReset/{email}")
    public ResponseEntity<Void> request(@Parameter(description = "User's email address") @PathVariable("email") String email) {
        boolean bool = loginService.requestPasswordReset(email);
        if (bool)
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        else
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .header("Error-Message", "Error with email")
                    .build();
    }

    @Operation(summary = "Reset password", description = "Reset user password using reset token")
    @PostMapping(value = "/reset")
    public ResponseEntity<LoginResponseDto> reset(@RequestBody @Valid LoginResetDto loginResetDto) {
        LoginResponseDto loginResponseDto = loginService.resetPassword(loginMapper.loginResetDtoToUser(loginResetDto));
        if (loginResponseDto.id() != -1L)
            return ResponseEntity.ok(loginResponseDto);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponseDto);
    }
}
