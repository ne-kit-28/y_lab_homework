package y_lab.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for validating email addresses.
 * This class provides a method to check the validity of email formats based on a regex pattern.
 */
public class EmailValidatorService {

    // Regex pattern for validating email addresses
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    /**
     * Checks if the provided email address is valid.
     *
     * @param email the email address to be validated
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
