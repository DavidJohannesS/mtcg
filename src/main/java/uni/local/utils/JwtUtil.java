package uni.local.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
   // private static final String SECRET_KEY = "admin-mtcgToken";
    private static final String SECRET_KEY = System.getenv("JWT_SECRET");
    public static String generateToken(String username, int userId) {
        return Jwts.builder()
                   .setSubject(username)
                   .claim("userId", userId)  // Include the userId claim
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 1516239022)) // 1 day expiration
                   .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                   .compact();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY)
                   .parseClaimsJws(token)
                   .getBody();
    }

    public static boolean isTokenValid(String token, String username) {
        Claims claims = extractClaims(token);
        return claims.getSubject().equals(username) && !claims.getExpiration().before(new Date());
    }

    public static String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    // Update this method to handle the absence of the userId claim
    public static int extractUserId(String token) {
        Claims claims = extractClaims(token);
        Object userId = claims.get("userId");
        if (userId == null) {
            throw new IllegalArgumentException("Token does not contain userId claim");
        }
        return Integer.parseInt(userId.toString());
    }
}

