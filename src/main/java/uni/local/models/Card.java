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
    private Integer packageId;
    private Integer ownerId;

    public enum CardName {
        WaterGoblin, FireGoblin, RegularGoblin,
        WaterTroll, FireTroll, RegularTroll,
        WaterElf, FireElf, RegularElf,
        WaterSpell, FireSpell, RegularSpell,
        Knight, Dragon, Ork, Kraken, Wizard 
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isSpell() {
        return name.name().contains("Spell");
    }

    public String getElementType() {
        if (name.name().startsWith("Fire")) {
            return "Fire";
        } else if (name.name().startsWith("Water")) {  
            return "Water";
        } else {
            return "Normal";
        }
    }

    public boolean isMonster() {
        return !isSpell();
    }
}

