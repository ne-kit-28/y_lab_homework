package y_lab.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailValidatorServiceTest {

    @Test
    void isValid_shouldReturnTrue_forValidEmail() {
        // Arrange
        String validEmail = "test@example.com";

        // Act
        boolean isValid = EmailValidatorService.isValid(validEmail);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_forInvalidEmailWithoutAtSymbol() {
        // Arrange
        String invalidEmail = "testexample.com";

        // Act
        boolean isValid = EmailValidatorService.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_forInvalidEmailWithMultipleAtSymbols() {
        // Arrange
        String invalidEmail = "test@@example.com";

        // Act
        boolean isValid = EmailValidatorService.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_forInvalidEmailWithSpaces() {
        // Arrange
        String invalidEmail = "test @example.com";

        // Act
        boolean isValid = EmailValidatorService.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_forInvalidEmailWithSpecialCharacters() {
        // Arrange
        String invalidEmail = "test!@example.com";

        // Act
        boolean isValid = EmailValidatorService.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_forNullEmail() {
        // Arrange
        String nullEmail = null;

        // Act
        boolean isValid = EmailValidatorService.isValid(nullEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_forEmptyEmail() {
        // Arrange
        String emptyEmail = "";

        // Act
        boolean isValid = EmailValidatorService.isValid(emptyEmail);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_forEmailWithSubdomain() {
        // Arrange
        String validEmail = "test@mail.example.com";

        // Act
        boolean isValid = EmailValidatorService.isValid(validEmail);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void isValid_shouldReturnTrue_forEmailWithPlusSign() {
        // Arrange
        String validEmail = "test+tag@example.com";

        // Act
        boolean isValid = EmailValidatorService.isValid(validEmail);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_forInvalidEmailWithTooLongDomain() {
        // Arrange
        String invalidEmail = "test@averylongdomainname.com123456";

        // Act
        boolean isValid = EmailValidatorService.isValid(invalidEmail);

        // Assert
        assertThat(isValid).isFalse();
    }
}
