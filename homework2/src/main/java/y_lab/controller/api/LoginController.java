package y_lab.controller.api;

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
import y_lab.service.serviceImpl.LoginServiceImpl;
import y_lab.util.JwtUtil;


@RestController
@RequestMapping(value = "/login")
public class LoginController {

    private final LoginMapper loginMapper;
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginServiceImpl loginService) {
        this.loginService = loginService;
        loginMapper = new LoginMapperImpl();
    }

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

    @PostMapping(value = "/signUp")
    public ResponseEntity<LoginResponseDto> signUp(@RequestBody @Valid LoginUpDto loginUpDto) {
        LoginResponseDto loginResponseDto = loginService.register(loginMapper.loginUpDtoToUser(loginUpDto));
        if (loginResponseDto.id() != -1L)
            return ResponseEntity.ok(loginResponseDto);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponseDto);
    }

    @PostMapping(value = "/requestReset/{email}")
    public ResponseEntity<Void> request(@PathVariable("email") String email) {
        boolean bool = loginService.requestPasswordReset(email);
        if (bool)
            return ResponseEntity
                    .status(201)
                    .build();
        else
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .header("Error-Message", "Error with email")
                    .build();
    }

    @PostMapping(value = "/reset")
    public ResponseEntity<LoginResponseDto> reset(@RequestBody @Valid LoginResetDto loginResetDto) {
        LoginResponseDto loginResponseDto = loginService.resetPassword(loginMapper.loginResetDtoToUser(loginResetDto));
        if (loginResponseDto.id() != -1L)
            return ResponseEntity.ok(loginResponseDto);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponseDto);
    }
}
