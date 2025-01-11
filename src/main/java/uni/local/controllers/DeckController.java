package uni.local.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uni.local.models.Card;
import uni.local.services.DeckService;
import uni.local.utils.JwtUtil;
import uni.local.utils.http.ResponseService;

import java.io.IOException;
import java.util.List;

public class DeckController {
    private final DeckService deckService = DeckService.getInstance();
    private final ResponseService responseService = new ResponseService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getDeck(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        String token = authHeader.substring(7);
        int userId = JwtUtil.extractUserId(token);

        List<Card> userDeck = deckService.getDeck(userId);
        try {
            String jsonResponse = objectMapper.writeValueAsString(userDeck);
            return responseService.createSuccessResponse(200, jsonResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(500, "Internal Server Error");
        }
    }

    public String setDeck(String requestBody, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        String token = authHeader.substring(7);
        int userId = JwtUtil.extractUserId(token);

        try {
            List<String> cardIds = objectMapper.readValue(requestBody, new TypeReference<List<String>>() {});
            boolean success = deckService.setDeck(userId, cardIds);
            if (success) {
                return responseService.createSuccessResponse(200, "Deck updated successfully");
            } else {
                return responseService.createErrorResponse(400, "Failed to update deck");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(400, "Invalid JSON format");
        } catch (IOException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(500, "Internal Server Error");
        }
    }
}

