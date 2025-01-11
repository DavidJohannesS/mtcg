package uni.local;

import uni.local.controllers.UserController;
import uni.local.controllers.PackageController;
import uni.local.utils.http.ResponseService;
import uni.local.utils.http.Constants;
import uni.local.controllers.CardController;
import uni.local.controllers.DeckController;
import uni.local.controllers.TradeController;
public class RestHandler {
    private final UserController userController = new UserController();
    private final PackageController packageController = new PackageController();
    private final ResponseService responseService = new ResponseService();
    private final String teapot = " The server refuses to brew coffee because it is, permanently, a teapot!";
    private final CardController cardController = new CardController();
    private final DeckController deckController = new DeckController();
    private final TradeController tradeController = new TradeController();
    public String handleRequest(String request, String requestBody, String authHeader) {
        if(request.startsWith("GET /cards"))
        {
            return cardController.getUserCards(authHeader);
        }
        else if (request.startsWith("POST /users")) {
            return userController.register(requestBody);

        } else if (request.startsWith("POST /sessions")) {
            return userController.login(requestBody);

        } else if (request.startsWith("POST /packages")) {
            return packageController.createPackage(requestBody, authHeader);

        } else if (request.startsWith("POST /transactions/packages"))
        {
                return packageController.buyPackage(authHeader);

        } else if (request.startsWith("GET /users")) {
            return responseService.createErrorResponse(418, teapot);
        } else if (request.startsWith("PUT /users")) {
            return responseService.createErrorResponse(418, teapot);
        } else if (request.startsWith("DELETE /users")) {
            return responseService.createErrorResponse(418, teapot);
        } else if (request.startsWith("PATCH /users")) {
            return responseService.createErrorResponse(418, teapot);
        } else if (request.startsWith("HEAD /users")) {
            return responseService.createErrorResponse(418, teapot);
        } else if (request.startsWith("OPTIONS /users")) {
            return responseService.createErrorResponse(418, teapot);
        } else if (request.startsWith("TRACE /users")) {
            return responseService.createErrorResponse(418, teapot);
        } else if (request.startsWith("GET /deck"))
        { return deckController.getDeck(authHeader);
        } else if (request.startsWith("GET /tradings"))
        {
            return tradeController.getTradingDeals(authHeader);
        } else if (request.startsWith("POST /tradings"))
        {
            return tradeController.createTradingDeal(requestBody, authHeader);
        } else if (request.startsWith("DELETE /tradings"))
        {
            return tradeController.deleteTradingDeal(requestBody, authHeader);
        } 


        else if (request.startsWith("PUT /deck"))
        {
            return deckController.setDeck(requestBody, authHeader);
        } else {
            return responseService.createErrorResponse(Constants.STATUS_BAD_REQUEST, "Endpoint not found");
        }
}
    
}

