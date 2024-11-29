package uni.local;

import uni.local.controllers.UserController;
import uni.local.utils.http.ResponseService;
import uni.local.utils.http.Constants;

public class RestHandler
{
    private final UserController userController = new UserController();
    private final ResponseService responseService = new ResponseService();
    private final String teapot = " The server refuses to brew coffee because it is, permanently, a teapot!";
    public String handleRequest(String request, String requestBody )
    {
        if ( request.startsWith( "POST /users"  )) {
            return userController.register( requestBody  );

        } else if ( request.startsWith( "POST /sessions" )) {
            return userController.login( requestBody );

        } else if ( request.startsWith( "GET /users" )) {
            return responseService.createErrorResponse( Constants.STATUS_NOT_IMPLEMENTED, teapot );
        } else if ( request.startsWith( "PUT /users" )) {
            return responseService.createErrorResponse( Constants.STATUS_NOT_IMPLEMENTED, teapot );
        } else if ( request.startsWith( "DELETE /users" )) {
            return responseService.createErrorResponse( Constants.STATUS_NOT_IMPLEMENTED, teapot );
        } else if ( request.startsWith( "PATCH /users" )) {
            return responseService.createErrorResponse( Constants.STATUS_NOT_IMPLEMENTED, teapot );
        } else if ( request.startsWith( "HEAD /users" )) {
            return responseService.createErrorResponse( Constants.STATUS_NOT_IMPLEMENTED, teapot );
        } else if ( request.startsWith( "OPTIONS /users" )) {
            return responseService.createErrorResponse( Constants.STATUS_NOT_IMPLEMENTED, teapot );
        } else if ( request.startsWith( "TRACE /users" )) {
            return responseService.createErrorResponse( Constants.STATUS_NOT_IMPLEMENTED, teapot );
        } else {
            return responseService.createErrorResponse( Constants.STATUS_BAD_REQUEST, "Endpoint not found" );
        }
    }
}

