package uni.local.services;

import org.mindrot.jbcrypt.BCrypt;
import uni.local.models.User;
import uni.local.utils.TokenGenerator;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static UserService instance;
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> tokens = new HashMap<>();

    private UserService() {}

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("User already exists: " + username);
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        users.put(username, new User(username, hashedPassword));
        System.out.println("User registered: " + username + ", password: " + password + ", hashedPassword: " + hashedPassword);
        return true;
    }

    public String loginUser(String username, String password) {
        System.out.println("Login attempt for user: " + username + ", password: " + password);
        User user = users.get(username);
        if (user != null) {
            System.out.println("Stored hashedPassword: " + user.getPassword());
            if (BCrypt.checkpw(password, user.getPassword())) {
                String token = TokenGenerator.generateToken(username);
                tokens.put(username, token);
                System.out.println("User logged in: " + username);
                return token;
            } else {
                System.out.println("Password mismatch for user: " + username);
            }
        } else {
            System.out.println("User not found: " + username);
        }
        System.out.println("Invalid login attempt for user: " + username);
        return null;
    }
}
