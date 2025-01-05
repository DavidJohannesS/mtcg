package uni.local;

import io.jsonwebtoken.Jwts;
import uni.local.utils.JwtUtil;

public class Main {
    public static void main(String[] args) {
        String token = JwtUtil.generateToken("admin", 01);
        System.out.println(token);
        Server server = new Server();
        server.start();
    }
}
