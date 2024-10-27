package y_lab.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailValidatorTest {

    @Test
    @DisplayName("should return true for valid email" +
            ", should return false for invalid email without at symbol" +
            ", should return false for invalid email with multiple at symbol" +
            ", should return false for invalid email with spaces" +
            ", should return false for invalid email with special characters")
    void isValid_() {
        // Arrange
        String validEmail = "test@example.com";

        // Act
        boolean isValid = EmailValidator.isValid(validEmail);

        // Assert
        assertThat(isValid).isTrue();

        String invalidEmail = "testexample.com";

        // Act
        isValid = EmailValidator.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();

        invalidEmail = "test@@example.com";

        // Act
        isValid = EmailValidator.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();

        invalidEmail = "test @example.com";

        // Act
        isValid = EmailValidator.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();

        invalidEmail = "test!@example.com";

        // Act
        isValid = EmailValidator.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();
    }


    @Test
    @DisplayName("null email")
    void isValidNullName() {
        // Arrange
        String nullEmail = null;

        // Act
        boolean isValid = EmailValidator.isValid(nullEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("should return false for empty email")
    void isValidEmptyEmail() {
        // Arrange
        String emptyEmail = "";

        // Act
        boolean isValid = EmailValidator.isValid(emptyEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("should return true for email with subdomain")
    void isValidWithSubdomain() {
        // Arrange
        String validEmail = "test@mail.example.com";

        // Act
        boolean isValid = EmailValidator.isValid(validEmail);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("should return true for email with plus sign")
    void isValidPlusSign() {
        // Arrange
        String validEmail = "test+tag@example.com";

        // Act
        boolean isValid = EmailValidator.isValid(validEmail);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("should return false for valid email with too long domain")
    void isValidFalseTooLongDomain() {
        // Arrange
        String invalidEmail = "test@averylongdomainname.com123456";

        // Act
        boolean isValid = EmailValidator.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();
    }
}
