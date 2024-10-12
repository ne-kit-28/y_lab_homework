package y_lab.usecases.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashFunctionTest {

    @Test
    void hashPassword_shouldReturnSameHash_forSamePassword() {
        // Arrange
        String password = "securePassword";

        // Act
        String hash1 = HashFunction.hashPassword(password);
        String hash2 = HashFunction.hashPassword(password);

        // Assert
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void hashPassword_shouldReturnDifferentHashes_forDifferentPasswords() {
        // Arrange
        String password1 = "password123";
        String password2 = "differentPassword";

        // Act
        String hash1 = HashFunction.hashPassword(password1);
        String hash2 = HashFunction.hashPassword(password2);

        // Assert
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void hashPassword_shouldReturnSameHash_forIdenticalPasswordsWithDifferentCases() {
        // Arrange
        String password1 = "Password";
        String password2 = "password";

        // Act
        String hash1 = HashFunction.hashPassword(password1);
        String hash2 = HashFunction.hashPassword(password2);

        // Assert
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void hashPassword_shouldReturnHash_forEmptyPassword() {
        // Arrange
        String password = "";

        // Act
        String hash = HashFunction.hashPassword(password);

        // Assert
        assertThat(hash).isEqualTo(Integer.toHexString("".hashCode()));
    }


    @Test
    void hashPassword_shouldReturnHash_forSpecialCharacters() {
        // Arrange
        String password = "!@#$%^&*()";

        // Act
        String hash = HashFunction.hashPassword(password);

        // Assert
        assertThat(hash).isEqualTo(Integer.toHexString(password.hashCode()));
    }

    @Test
    void hashPassword_shouldReturnHash_forLongPassword() {
        // Arrange
        String password = "thisIsAVeryLongPasswordThatExceedsTypicalLength";

        // Act
        String hash = HashFunction.hashPassword(password);

        // Assert
        assertThat(hash).isEqualTo(Integer.toHexString(password.hashCode()));
    }
}
