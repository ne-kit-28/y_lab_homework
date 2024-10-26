package y_lab.dto;

import jakarta.validation.constraints.Email;

public record UserRequestDto(
        @Email
        String email,
        String name,
        String password
) {
}
