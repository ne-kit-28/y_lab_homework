package y_lab.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.util.Optional;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User management operations")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.userMapper = new UserMapperImpl();
    }

    @Operation(summary = "Get user by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/{userId}/{email}")
    public ResponseEntity<UserResponseDto> getUser(
            @PathVariable("userId") @Positive long userId,
            @PathVariable("email") @Email String email) {
        Optional<User> user = userService.getUser(email);
        return user.map(value -> ResponseEntity.ok(userMapper.userToUserResponseDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PutMapping(value = "/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") @Positive long userId,
                                           @RequestBody @Valid UserRequestDto userRequestDto) {
        if (userService.editUser(userId, userMapper.userRequestDtoToUser(userRequestDto))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") @Positive long userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
