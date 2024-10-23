package y_lab.dto;

public record UserRequestDto(
        String email,
        String name,
        String password
) {
}
