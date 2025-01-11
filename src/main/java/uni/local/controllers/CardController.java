package uni.local.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uni.local.models.Card;
import uni.local.services.CardService;
import uni.local.utils.JwtUtil;
import uni.local.utils.http.ResponseService;

import java.util.List;

public class CardController {
    private final CardService cardService = CardService.getInstance();
    private final ResponseService responseService = new ResponseService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getUserCards(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        String token = authHeader.substring(7);
        int userId = JwtUtil.extractUserId(token);

        List<Card> userCards = cardService.getUserCards(userId);
        try {
            String jsonResponse = objectMapper.writeValueAsString(userCards);
            return responseService.createSuccessResponse(200, jsonResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(500, "Internal Server Error");
        }
    }
}

