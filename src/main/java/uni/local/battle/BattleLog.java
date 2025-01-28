package uni.local.battle;

import java.util.ArrayList;
import java.util.List;

public class BattleLog {
    private List<String> logEntries;

    public BattleLog() {
        logEntries = new ArrayList<>();
    }

    public void addEntry(String entry) {
        logEntries.add(entry);
    }

    public String getFullLog() {
        return String.join("\n", logEntries);
    }
}

