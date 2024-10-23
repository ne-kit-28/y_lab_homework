package y_lab.dto;

public record UserResponseDto(
        String email,
        String name,
        Boolean isBlock
) {
}
