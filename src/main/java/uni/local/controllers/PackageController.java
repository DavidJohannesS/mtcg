package uni.local.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import uni.local.models.Card;
import uni.local.models.User;
import uni.local.services.PackageService;
import uni.local.services.UserService;
import uni.local.utils.JwtUtil;
import uni.local.utils.http.ResponseService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PackageController {
    private final PackageService packageService = PackageService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final ResponseService responseService = new ResponseService();

    public String createPackage(String requestBody, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.isTokenValid(token, "admin")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        List<Card> cards = parseRequestBody(requestBody);
        try {
            boolean success = packageService.createPackage(cards);
            if (success) {
                return responseService.createSuccessResponse(201, "Package created");
            } else {
                return responseService.createErrorResponse(500, "Failed to create package");
            }
        } catch (IllegalArgumentException e) {
            return responseService.createErrorResponse(400, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(500, "Internal Server Error");
        }
    }

    public String buyPackage(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }

        String token = authHeader.substring(7);
        System.out.println(authHeader);
        int userId = JwtUtil.extractUserId(token);
        User user = userService.getUserById(userId);
        System.out.println(userId);
        if (user == null) {
            return responseService.createErrorResponse(404, "User not found");
        }

        try {
            boolean success = packageService.buyPackage(user);
            if (success) {
                return responseService.createSuccessResponse(200, "Package purchased successfully");
            } else {
                return responseService.createErrorResponse(500, "Failed to purchase package");
            }
        } catch (IllegalArgumentException e) {
            return responseService.createErrorResponse(400, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return responseService.createErrorResponse(500, "Internal Server Error");
        }
    }

    private List<Card> parseRequestBody(String requestBody) {
        List<Card> cards = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(requestBody);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            Card card = new Card();
            card.setId(UUID.fromString(json.getString("Id")));
            card.setName(Card.CardName.valueOf(json.getString("Name")));
            card.setDamage((float) json.getDouble("Damage"));
            cards.add(card);
        }
        return cards;
    }
}

