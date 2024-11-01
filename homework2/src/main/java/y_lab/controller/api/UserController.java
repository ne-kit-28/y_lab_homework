package y_lab.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import y_lab.domain.User;
import y_lab.dto.UserRequestDto;
import y_lab.dto.UserResponseDto;
import y_lab.mapper.UserMapper;
import y_lab.mapper.UserMapperImpl;
import y_lab.service.UserService;
import y_lab.service.serviceImpl.UserServiceImpl;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController (UserServiceImpl userService){
        this.userService = userService;
        this.userMapper = new UserMapperImpl();
    }

    @GetMapping(value = "/{userId}/{email}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("email") @Email String email) {
        Optional<User> user = userService.getUser(email);
        return user.map(value -> ResponseEntity.ok(userMapper.userToUserResponseDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") @Positive long userId,
                                           @RequestBody @Valid UserRequestDto userRequestDto) {
        if (userService.editUser(userId, userMapper.userRequestDtoToUser(userRequestDto)))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") @Positive long userId) {
        if (userService.deleteUser(userId))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
