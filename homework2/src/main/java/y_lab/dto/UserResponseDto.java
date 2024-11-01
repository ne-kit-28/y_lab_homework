package y_lab.dto;

public record UserResponseDto(
        long id,
        String email,
        String name,
        Boolean isBlock
) {
}
