package y_lab.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HabitRequestDto(
        @NotNull(message = "Name cannot be null")
        @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
        String name,
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        String description,
        @Pattern(regexp = "^(daily|weekly)$", message = "Frequency must be either 'daily' or 'weekly'")
        String frequency
) {
}
