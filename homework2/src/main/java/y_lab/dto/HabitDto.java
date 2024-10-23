package y_lab.dto;

public record HabitDto(
        Long userId,
        String name,
        String description,
        String frequency,
        String createAt
) {
}
