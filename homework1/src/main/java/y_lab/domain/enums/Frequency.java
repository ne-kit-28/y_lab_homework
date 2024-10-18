package y_lab.domain.enums;

public enum Frequency {
    DAILY,      // Ежедневно
    WEEKLY;     // Еженедельно

    public String getValue() {
        return this.name();
    }

    public static Frequency fromString(String frequencyString) {
        try {
            return Frequency.valueOf(frequencyString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid frequency: " + frequencyString);
        }
    }
}
