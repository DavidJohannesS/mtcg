package uni.local.battle;

import uni.local.models.User;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class BattleManager {
    private static BattleManager instance;
    private ConcurrentLinkedQueue<User> battleQueue;
    private ConcurrentMap<Integer, String> battleResults; // Map userId to battle result

    private BattleManager() {
        battleQueue = new ConcurrentLinkedQueue<>();
        battleResults = new ConcurrentHashMap<>();
    }

    public static synchronized BattleManager getInstance() {
        if (instance == null) {
            instance = new BattleManager();
        }
        return instance;
    }

    public void enqueuePlayer(User user) {
        battleQueue.add(user);
        System.out.println(user.getUsername() + " added to the battle queue.");
        startBattles();
    }

    private void startBattles() {
        while (battleQueue.size() >= 2) {
            User playerA = battleQueue.poll();
            User playerB = battleQueue.poll();
            if (playerA != null && playerB != null) {
                System.out.println("Initiating battle between " + playerA.getUsername() + " and " + playerB.getUsername());
                Battle battle = new Battle(playerA, playerB);
                String battleLog = battle.start(); 
                // Store battle results for both players
                battleResults.put(playerA.getId(), battleLog);
                battleResults.put(playerB.getId(), battleLog);
            }
        }
    }
public String getBattleResult(int userId) {
        return battleResults.get(userId); 
    }
}

