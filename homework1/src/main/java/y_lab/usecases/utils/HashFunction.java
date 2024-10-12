package y_lab.usecases.utils;

/**
 * Utility class for hashing passwords.
 * This class provides methods for generating hashed representations of passwords.
 */
public class HashFunction {
    /**
     * Hashes a given password using the hash code method.
     * This method generates a hexadecimal string representation of the password's hash code.
     *
     * @param password the password to be hashed
     * @return a hexadecimal string representing the hashed password
     */
    public static String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
}
