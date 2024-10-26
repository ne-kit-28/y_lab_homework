package y_lab.dto;

public record LoginResponseDto(
        long id,
        String email,
        String password,
        String message
) {
}
