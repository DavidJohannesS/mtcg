package uni.local.models;

import lombok.Data;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Card {
    private UUID id;
    private CardName name;
    private float damage;
    private int package_id;
    private int owner_id;

    public enum CardName {
        WaterGoblin, FireGoblin, RegularGoblin,
        WaterTroll, FireTroll, RegularTroll,
        WaterElf, FireElf, RegularElf,
        WaterSpell, FireSpell, RegularSpell,
        Knight, Dragon, Ork, Kraken, Wizzard
    }
}

