package uni.local.controllers;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uni.local.models.Trade;
import uni.local.services.TradeService;
import uni.local.utils.JwtUtil;
import uni.local.utils.http.ResponseService;

import java.io.IOException;
import java.util.UUID;

public class TradeController {
    private final TradeService tradeService = TradeService.getInstance();
    private final ResponseService responseService = new ResponseService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // GET /tradings
    public String getTradingDeals(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        List<Trade> trades = tradeService.getAllTrades();

        try {
            String jsonResponse = objectMapper.writeValueAsString(trades);
            return responseService.createSuccessResponse(200, jsonResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(500, "Internal Server Error");
        }
    }

    // POST /tradings
    public String createTradingDeal(String requestBody, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        int userId = JwtUtil.extractUserId(authHeader.substring(7));

        try {
            Trade trade = objectMapper.readValue(requestBody, Trade.class);
            trade.setOwnerId(userId);

            boolean success = tradeService.createTrade(trade);
            if (success) {
                return responseService.createSuccessResponse(201, "Trade created successfully");
            } else {
                return responseService.createErrorResponse(400, "Failed to create trade");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(400, "Invalid JSON format");
        } catch (IOException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(500, "Internal Server Error");
        }
    }

    // POST /tradings/{id}
public String acceptTradingDeal(String request, String requestBody, String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return responseService.createErrorResponse(401, "Unauthorized");
    }

    int userId = JwtUtil.extractUserId(authHeader.substring(7));

    // Extract the trade ID from the URL
    String tradeIdStr = request.substring(request.indexOf("/tradings/") + 10);
    UUID tradeId = UUID.fromString(tradeIdStr);

    try {
        // Since the request body contains just the offered card ID, we can parse it directly
        String offeredCardIdStr = requestBody.replace("\"", "").trim();  // Remove quotes and whitespace
        UUID offeredCardId = UUID.fromString(offeredCardIdStr);

        boolean success = tradeService.acceptTrade(tradeId, offeredCardId, userId);
        if (success) {
            return responseService.createSuccessResponse(200, "Trade accepted successfully");
        } else {
            return responseService.createErrorResponse(400, "Failed to accept trade");
        }
    } catch (IllegalArgumentException e) {
        return responseService.createErrorResponse(400, e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        return responseService.createErrorResponse(500, "Internal Server Error");
    }
}



    // DELETE /tradings/{id}
    public String deleteTradingDeal(String request, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        int userId = JwtUtil.extractUserId(authHeader.substring(7));
        String tradeIdStr = request.split("/tradings/")[1];  // Extract the ID from the URL
        UUID tradeId = UUID.fromString(tradeIdStr);

        boolean success = tradeService.deleteTrade(tradeId);
        if (success) {
            return responseService.createSuccessResponse(200, "Trade deleted successfully");
        } else {
            return responseService.createErrorResponse(400, "Failed to delete trade");
        }
    }
}

