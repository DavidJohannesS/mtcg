package uni.local.controllers;

import uni.local.battle.BattleManager;
import uni.local.models.User;
import uni.local.services.UserService;
import uni.local.utils.JwtUtil;
import uni.local.utils.http.ResponseService;
public class BattleController {
    private final ResponseService responseService = new ResponseService();
    private final UserService userService = UserService.getInstance();
    private final BattleManager battleManager = BattleManager.getInstance();

    public String requestBattle(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }
        String token = authHeader.substring(7);
        int userId = JwtUtil.extractUserId(token);
        User user = userService.getUserById(userId);
        if (user == null) {
            return responseService.createErrorResponse(404, "User not found");
        }

        // Check if the user has a pending battle result
        String battleResult = battleManager.getBattleResult(userId);
        if (battleResult != null) {
            return responseService.createSuccessResponse(200, battleResult);
        }

        // Enqueue player for battle
        battleManager.enqueuePlayer(user);

        // Return message indicating that the user is waiting for a battle
        return responseService.createSuccessResponse(200, "Added to battle queue. Waiting for opponent.");
    }

    public String retrieveBattleResult(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responseService.createErrorResponse(401, "Unauthorized");
        }
        String token = authHeader.substring(7);
        int userId = JwtUtil.extractUserId(token);

        // Retrieve the battle result
        String battleResult = battleManager.getBattleResult(userId);
        if (battleResult != null) {
            return responseService.createSuccessResponse(200, battleResult);
        } else {
            return responseService.createErrorResponse(204, "No battle result available.");
        }
    }
}

