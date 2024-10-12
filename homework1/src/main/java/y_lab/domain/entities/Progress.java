package y_lab.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Progress implements Serializable {
    private Long id;
    private User user;
    private Habit habit;
    private LocalDate date;
}
