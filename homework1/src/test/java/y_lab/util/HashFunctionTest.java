package y_lab.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashFunctionTest {

    @Test
    @DisplayName("Should return the same hash for the same password")
    void hashPasswordForSamePassword() {
        // Arrange
        String password = "securePassword";

        // Act
        String hash1 = HashFunction.hashPassword(password);
        String hash2 = HashFunction.hashPassword(password);

        // Assert
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Should return different hashes for different passwords")
    void hashPasswordForDifferentPasswords() {
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
    @DisplayName("Should return different hashes for identical passwords with different cases")
    void hashPasswordIdenticalPasswordsWithDifferentCases() {

        String password1 = "Password";
        String password2 = "password";

        String hash1 = HashFunction.hashPassword(password1);
        String hash2 = HashFunction.hashPassword(password2);

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Should return a hash for an empty password")
    void hashPasswordEmptyPassword() {

        String password = "";

        String hash = HashFunction.hashPassword(password);

        assertThat(hash).isEqualTo(Integer.toHexString("".hashCode()));
    }

    @Test
    @DisplayName("Should return a hash for passwords with special characters")
    void hashPasswordSpecialCharacters() {

        String password = "!@#$%^&*()";

        String hash = HashFunction.hashPassword(password);

        assertThat(hash).isEqualTo(Integer.toHexString(password.hashCode()));
    }

    @Test
    @DisplayName("Should return a hash for a long password")
    void hashPasswordLongPassword() {

        String password = "thisIsAVeryLongPasswordThatExceedsTypicalLength";

        String hash = HashFunction.hashPassword(password);

        assertThat(hash).isEqualTo(Integer.toHexString(password.hashCode()));
    }
}
