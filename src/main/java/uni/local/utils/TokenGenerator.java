package uni.local.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    public static String generateToken(String username) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return username + "-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
