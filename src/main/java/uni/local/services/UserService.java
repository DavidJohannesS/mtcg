package uni.local.services;

import org.mindrot.jbcrypt.BCrypt;
import uni.local.models.User;
import uni.local.repository.UserRepository;
import uni.local.utils.JwtUtil;

import java.sql.SQLException;

public class UserService {
    private static UserService instance;
    private final UserRepository userRepository;

    private UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService(new UserRepository());
        }
        return instance;
    }
 public void updateUser(User user) 
 { 
     userRepository.updateUser(user);
 }
 public void updateUserElo(User user) {
    userRepository.updateUserElo(user.getId(), user.getElo());
}

    public boolean registerUser(String username, String password) {
        try {
            if (userRepository.existsByUsername(username)) {
                System.out.println("User already exists: " + username);
                return false;
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.save(new User(username, hashedPassword, 20, 0));
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
                String token = JwtUtil.generateToken(username, user.getId());
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

    public User getUserById(int userId) {
        return userRepository.findById(userId);
    }
public void deleteUserByUsername(String username) {
    userRepository.deleteByUsername(username);
}

public User getUserByUsername(String username) {
    try {
        return userRepository.findByUsername(username);
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}

}

