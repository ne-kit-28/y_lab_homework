package y_lab.dto;

public record LoginResetDto(
        String email,
        String password,
        String token
) {
}
