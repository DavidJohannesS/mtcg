package uni.local.controllers;

import org.json.JSONObject;
import uni.local.services.UserService;
import uni.local.utils.http.*;

public class UserController
{
    private final UserService userService = UserService.getInstance();
    private final ResponseService responseService = new ResponseService();

    public String register( String requestBody ) {

        JSONObject json = new JSONObject( requestBody );
        String username = json.getString( "Username" );
        String password = json.getString( "Password" );

        boolean isRegistered = userService.registerUser( username, password );
        return isRegistered ? responseService.createSuccessResponse( Constants.STATUS_CREATED, "User registered successfully." )
                            : responseService.createErrorResponse( Constants.STATUS_BAD_REQUEST, "User already registered." );
    }

    public String login( String requestBody )
    {
        JSONObject json = new JSONObject (requestBody );
        String username = json.getString( "Username" );
        String password = json.getString( "Password" );

        String token = userService.loginUser( username, password );
        return token != null ? responseService.createTokenResponse( token )
                             : responseService.createErrorResponse( Constants.STATUS_UNAUTHORIZED, "Invalid username or password." );
    }
}
