package uni.local.controllers;

import uni.local.models.Card;
import uni.local.services.CardService;
import uni.local.utils.JwtUtil;
import uni.local.utils.http.ResponseService;

import java.util.List;

public class CardController {
    private final CardService cardService = CardService.getInstance();
    private final ResponseService responseService = new ResponseService();

    public String getUserCards(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        String token = authHeader.substring(7);
        int userId = JwtUtil.extractUserId(token);

        List<Card> userCards = cardService.getUserCards(userId);
        return responseService.createSuccessResponse(200, userCards);
    }
}

