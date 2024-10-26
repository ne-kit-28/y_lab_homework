package y_lab.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import y_lab.domain.enums.Role;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "secret_key";
    private static final long EXPIRATION_TIME = 600000; // 10 минут

    // Генерация токена
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

    // Валидация токена
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    // Получение ID пользователя из токена
    public static Long getUserId(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Long.class);
    }

    // Получение роли из токена
    public static String getRole(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }
}
