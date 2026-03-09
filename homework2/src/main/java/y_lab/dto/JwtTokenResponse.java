package y_lab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenResponse {
    private long id;
    private String role;
    private String token;
}
