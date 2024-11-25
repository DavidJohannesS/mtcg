package uni.local.controllers;

import org.json.JSONObject;
import uni.local.services.UserService;

public class UserController {
    private final UserService userService = UserService.getInstance();

    public String register(String requestBody) {
        JSONObject json = new JSONObject(requestBody);
        String username = json.getString("Username");
        String password = json.getString("Password");

        boolean isRegistered = userService.registerUser(username, password);
        return isRegistered ? "HTTP/1.1 201 Created\r\n\r\nUser registered successfully.\r\n"
                            : "HTTP/1.1 400 Bad Request\r\n\r\nUser already registered\r\n";
    }

    public String login(String requestBody) {
        JSONObject json = new JSONObject(requestBody);
        String username = json.getString("Username");
        String password = json.getString("Password");

        String token = userService.loginUser(username, password);
        return token != null ? "HTTP/1.1 200 OK\r\n\r\n" + token
                             : "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid username or password.\r\n";
    }
}

