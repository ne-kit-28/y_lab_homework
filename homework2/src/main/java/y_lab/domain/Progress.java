package y_lab.domain;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Progress implements Serializable {
    private Long id;
    private Long userId;
    private Long habitId;
    private LocalDate date;
}
