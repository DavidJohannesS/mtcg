package uni.local.utils.http;

import uni.local.utils.http.*;

public class ResponseService {

    public String createSuccessResponse(int statusCode, String message) {
        return ResponseBuilder.builder()
                .statusCode(statusCode)
                .contentType(Constants.CONTENT_TYPE_JSON)
                .body("{\"message\":\"" + message + "\"}")
                .build()
                .buildResponse();
    }

    public String createErrorResponse(int statusCode, String error) {
        return ResponseBuilder.builder()
                .statusCode(statusCode)
                .contentType(Constants.CONTENT_TYPE_JSON)
                .body("{\"error\":\"" + error + "\"}")
                .build()
                .buildResponse();
    }

    public String createTokenResponse(String token) {
        return ResponseBuilder.builder()
                .statusCode(Constants.STATUS_OK)
                .contentType(Constants.CONTENT_TYPE_JSON)
                .body("{\"token\":\"" + token + "\"}")
                .build()
                .buildResponse();
    }
}
