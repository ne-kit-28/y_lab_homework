package y_lab.domain;

import lombok.*;
import y_lab.domain.enums.Role;

import java.io.Serializable;


@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private Long id;
    private String email;
    private String passwordHash;
    private String name;
    private boolean isBlock = false;
    private Role role;
    private String resetToken;

    public User(String email, String passwordHash, String name, boolean isBlock, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.isBlock = isBlock;
        this.role = role;
    }
}
