package uni.local.services;

import org.mindrot.jbcrypt.BCrypt;
import uni.local.models.User;
import uni.local.repository.UserRepository;
import uni.local.utils.TokenGenerator;

import java.sql.SQLException;

public class UserService {
    private static UserService instance;
    private final UserRepository userRepository;

    private UserService() {
        this.userRepository = new UserRepository();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public boolean registerUser(String username, String password) {
        try {
            if (userRepository.existsByUsername(username)) {
                System.out.println("User already exists: " + username);
                return false;
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.save(new User(username, hashedPassword));
            System.out.println("User registered: " + username);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String loginUser(String username, String password) {
        try {
            User user = userRepository.findByUsername(username);
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                String token = TokenGenerator.generateToken(username);
                // Store the token if needed
                System.out.println("User logged in: " + username);
                return token;
            } else {
                System.out.println("Invalid login attempt for user: " + username);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

