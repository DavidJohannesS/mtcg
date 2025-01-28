package uni.local;

import io.jsonwebtoken.Jwts;
import uni.local.utils.JwtUtil;

public class Main {
    public static void main(String[] args) {
        String token = JwtUtil.generateToken("admin", 01);

        String token1 = JwtUtil.generateToken("altenhof", 03);

        System.out.println(token);
        System.out.println(token1);
        Server server = new Server();
        server.start();
    }
}
