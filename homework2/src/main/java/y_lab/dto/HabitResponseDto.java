package y_lab.dto;

public record HabitResponseDto(
        String name,
        String description,
        String frequency,
        String createAt
) {
}
