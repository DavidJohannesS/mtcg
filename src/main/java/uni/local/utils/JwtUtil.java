package uni.local.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "admin-mtcgToken"; // Change this to a secure key

    public static String generateToken(String username) {
        return Jwts.builder()
                   .setSubject(username)
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

}

