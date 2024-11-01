package y_lab.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating and validating JSON Web Tokens (JWT).
 */
public class JwtUtil {
    private static final String SECRET_KEY = "great_long_secret_key_which_can_be_use";
    private static final long EXPIRATION_TIME = 600000; // 10 минут

    /**
     * Generates a JWT token for a user with specified user ID and role.
     *
     * @param userId the user ID to be embedded in the token
     * @param role   the role of the user to be embedded in the token
     * @return a generated JWT token as a string
     */
    public static String generateToken(long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    /**
     * Validates a JWT token and retrieves the claims from it.
     *
     * @param token the JWT token to validate
     * @return the claims contained within the token if it is valid
     */
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the JWT token from which to extract the user ID
     * @return the user ID embedded in the token, or null if extraction fails
     */
    public static Long getUserId(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extracts the role from a JWT token.
     *
     * @param token the JWT token from which to extract the role
     * @return the role embedded in the token, or null if extraction fails
     */
    public static String getRole(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }
}
