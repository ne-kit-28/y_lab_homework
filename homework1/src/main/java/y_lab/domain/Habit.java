package y_lab.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import y_lab.domain.enums.Frequency;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Habit implements Serializable {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Frequency frequency;
    private LocalDate createdAt;

    public Habit(String name, String description, Frequency frequency, LocalDate now) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = now;
    }
}
