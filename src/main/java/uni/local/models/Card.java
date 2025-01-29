package uni.local.models;

import java.util.UUID;

public class Card {
    private UUID id;
    private CardName name;
    private float damage;
    private Integer packageId;
    private Integer ownerId;
    private boolean isInDeck;

    public enum CardName {
        WaterGoblin, FireGoblin, RegularGoblin,
        WaterTroll, FireTroll, RegularTroll,
        WaterElf, FireElf, RegularElf,
        WaterSpell, FireSpell, RegularSpell,
        Knight, Dragon, Ork, Kraken, Wizard 
    }

    // Manually created constructor
    public Card(UUID id, CardName name, float damage, Integer packageId, Integer ownerId, boolean isInDeck) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.packageId = packageId;
        this.ownerId = ownerId;
        this.isInDeck = isInDeck;
    }

    // Overloaded constructor without isInDeck
    public Card(UUID id, CardName name, float damage, Integer packageId, Integer ownerId) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.packageId = packageId;
        this.ownerId = ownerId;
        this.isInDeck = false;  // or true, depending on your default value
    }

    // Default constructor
    public Card() {}

    // Getters and Setters...

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CardName getName() {
        return name;
    }

    public void setName(CardName name) {
        this.name = name;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isInDeck() {
        return isInDeck;
    }

    public void setInDeck(boolean inDeck) {
        isInDeck = inDeck;
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

