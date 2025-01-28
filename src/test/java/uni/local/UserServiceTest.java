package uni.local.services;
import org.mindrot.jbcrypt.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uni.local.models.User;
import uni.local.repository.UserRepository;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;  // Injecting the service to test

    @Mock
    private UserRepository userRepository;  // Mocking the repository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mocks
    }

    @Test
    public void testRegisterUser_Success() throws SQLException {
        // Mock the repository method
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        boolean result = userService.registerUser("testuser", "password");

        assertTrue(result, "User should be registered successfully");
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() throws SQLException {
        // Mock the repository method
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        boolean result = userService.registerUser("existinguser", "password");

        assertFalse(result, "Registration should fail for existing username");
        verify(userRepository, times(1)).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

@Test
public void testLoginUser_Success() throws SQLException {
    String plaintextPassword = "password";
    String hashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());

    User mockUser = new User(1, "loginuser", hashedPassword, 20, 100);
    when(userRepository.findByUsername("loginuser")).thenReturn(mockUser);

    String token = userService.loginUser("loginuser", plaintextPassword);

    assertNotNull(token, "Login should return a token");
}


    @Test
    public void testLoginUser_InvalidCredentials() throws SQLException {
        // Mock the repository method
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        String token = userService.loginUser("nonexistentuser", "password");

        assertNull(token, "Login should fail and return null for invalid credentials");
    }

    @Test
    public void testUpdateUserElo() {
        User user = new User(1, "elouser", "password", 20, 100);
        user.setElo(200);

        userService.updateUserElo(user);

        verify(userRepository, times(1)).updateUserElo(user.getId(), user.getElo());
    }
}

