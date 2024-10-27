package y_lab.domain.enums;

public enum Role {
    ADMINISTRATOR,
    REGULAR;

    public String getValue() {
        return this.name();
    }

    public static Role fromString(String roleString) {
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleString);
        }
    }
}
