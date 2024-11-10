package y_lab.dto;

public record ProgressResponseDto(
        long habitId,
        String type,
        String message
) {
}
