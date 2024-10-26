package y_lab.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginUpDto(
        @NotNull(message = "Username cannot be null")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String name,
        @Email
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}
