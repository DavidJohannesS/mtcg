package uni.local.controllers;

import uni.local.services.UserService;
import java.io.IOException;

public class UserController {
    private final UserService userService = UserService.getInstance();

    public String register(String requestBody) {
        String[] userDetails = requestBody.split("&");
        String username = userDetails[0].split("=")[1];
        String password = userDetails[1].split("=")[1];

        boolean isRegistered = userService.registerUser(username, password);
        return isRegistered ? "HTTP/1.1 200 OK\r\n\r\nUser registered successfully." :
                              "HTTP/1.1 400 Bad Request\r\n\r\nUser registration failed.";
    }

    public String login(String requestBody) {
        String[] userDetails = requestBody.split("&");
        String username = userDetails[0].split("=")[1];
        String password = userDetails[1].split("=")[1];

        String token = userService.loginUser(username, password);
        return token != null ? "HTTP/1.1 200 OK\r\n\r\n" + token :
                               "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid username or password.";
    }
}
