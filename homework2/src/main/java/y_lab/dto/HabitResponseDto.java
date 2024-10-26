package y_lab.dto;

public record HabitResponseDto(
        Long id,
        String name,
        String description,
        String frequency,
        String createdAt
) {
}
