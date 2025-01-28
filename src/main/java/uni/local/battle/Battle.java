package uni.local.battle;

import uni.local.models.Card;
import uni.local.models.User;
import uni.local.services.DeckService;
import uni.local.services.UserService;

import java.util.List;
import java.util.Random;

public class Battle {
    private User playerA;
    private User playerB;
    private List<Card> deckA;
    private List<Card> deckB;
    private BattleLog battleLog;
    private int roundCount;
    private final int MAX_ROUNDS = 100;

    private DeckService deckService = DeckService.getInstance();
    private UserService userService = UserService.getInstance();

    public Battle(User playerA, User playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.deckA = deckService.getDeck(playerA.getId());
        this.deckB = deckService.getDeck(playerB.getId());
        this.battleLog = new BattleLog();
        this.roundCount = 0;

        // Added debug statements
        System.out.println("Player A's deck size: " + deckA.size());
        System.out.println("Player B's deck size: " + deckB.size());
    }

    // Modified start() method to return the battle log
    public String start() {
        battleLog.addEntry("Battle between " + playerA.getUsername() + " and " + playerB.getUsername() + " started.");
        System.out.println("Battle between " + playerA.getUsername() + " and " + playerB.getUsername() + " started.");

        while (!deckA.isEmpty() && !deckB.isEmpty() && roundCount < MAX_ROUNDS) {
            roundCount++;
            executeRound();
        }
        finalizeBattle();

        // Output the full battle log to console
        System.out.println("Battle Log:");
        System.out.println(battleLog.getFullLog());

        // Return the battle log
        return battleLog.getFullLog();
    }

    private void executeRound() {
        Card cardA = drawRandomCard(deckA);
        Card cardB = drawRandomCard(deckB);

        battleLog.addEntry("Round " + roundCount + ":");
        battleLog.addEntry(playerA.getUsername() + " plays " + cardA.getName() + " (Damage: " + cardA.getDamage() + ")");
        battleLog.addEntry(playerB.getUsername() + " plays " + cardB.getName() + " (Damage: " + cardB.getDamage() + ")");

        String specialResult = applySpecialRules(cardA, cardB);
        if (specialResult != null) {
            battleLog.addEntry(specialResult);
            System.out.println(specialResult);
            if (specialResult.contains("wins the round")) {
                if (specialResult.startsWith(playerA.getUsername())) {
                    updateDecksAfterRound(deckA, deckB, cardA, cardB);
                } else {
                    updateDecksAfterRound(deckB, deckA, cardB, cardA);
                }
            }
            return;
        }

        // Apply element effectiveness if at least one card is a spell
        if (cardA.isSpell() || cardB.isSpell()) {
            applyElementEffectiveness(cardA, cardB);
        }

        battleLog.addEntry(playerA.getUsername() + "'s card damage after effectiveness: " + cardA.getDamage());
        battleLog.addEntry(playerB.getUsername() + "'s card damage after effectiveness: " + cardB.getDamage());

        String winner = determineRoundWinner(cardA, cardB);
        battleLog.addEntry(winner);
        System.out.println(winner);

        if (winner.contains(playerA.getUsername())) {
            updateDecksAfterRound(deckA, deckB, cardA, cardB);
        } else if (winner.contains(playerB.getUsername())) {
            updateDecksAfterRound(deckB, deckA, cardB, cardA);
        } else {
            battleLog.addEntry("Round is a draw. No cards exchanged.");
            System.out.println("Round is a draw. No cards exchanged.");
        }
    }

    private Card drawRandomCard(List<Card> deck) {
        Random rand = new Random();
        return deck.get(rand.nextInt(deck.size()));
    }

    private String applySpecialRules(Card cardA, Card cardB) {
        String nameA = cardA.getName().name();
        String nameB = cardB.getName().name();

        // Goblins are too afraid of Dragons to attack.
        if (nameA.contains("Goblin") && nameB.contains("Dragon")) {
            return playerB.getUsername() + " wins the round (Goblin is afraid of Dragon).";
        }
        if (nameB.contains("Goblin") && nameA.contains("Dragon")) {
            return playerA.getUsername() + " wins the round (Goblin is afraid of Dragon).";
        }

        // Wizards can control Orks so they are not able to damage them.
        if (nameA.contains("Wizard") && nameB.contains("Ork")) {
            return playerA.getUsername() + " wins the round (Wizard controls Ork).";
        }
        if (nameB.contains("Wizard") && nameA.contains("Ork")) {
            return playerB.getUsername() + " wins the round (Wizard controls Ork).";
        }

        // The armor of Knights is so heavy that WaterSpells make them drown instantly.
        if (nameA.contains("Knight") && nameB.contains("WaterSpell")) {
            return playerB.getUsername() + " wins the round (Knight drowns).";
        }
        if (nameB.contains("Knight") && nameA.contains("WaterSpell")) {
            return playerA.getUsername() + " wins the round (Knight drowns).";
        }

        // The Kraken is immune against spells.
        if (nameA.contains("Kraken") && cardB.isSpell()) {
            return playerA.getUsername() + " wins the round (Kraken is immune to spells).";
        }
        if (nameB.contains("Kraken") && cardA.isSpell()) {
            return playerB.getUsername() + " wins the round (Kraken is immune to spells).";
        }

        // The FireElves know Dragons since they were little and can evade their attacks.
        if (nameA.contains("FireElf") && nameB.contains("Dragon")) {
            return playerA.getUsername() + " wins the round (FireElf evades Dragon).";
        }
        if (nameB.contains("FireElf") && nameA.contains("Dragon")) {
            return playerB.getUsername() + " wins the round (FireElf evades Dragon).";
        }

        return null;  // No special rules applied
    }

    private void applyElementEffectiveness(Card cardA, Card cardB) {
        float originalDamageA = cardA.getDamage();
        float originalDamageB = cardB.getDamage();

        float multiplierA = getElementMultiplier(cardA.getElementType(), cardB.getElementType());
        float multiplierB = getElementMultiplier(cardB.getElementType(), cardA.getElementType());

        cardA.setDamage(originalDamageA * multiplierA);
        cardB.setDamage(originalDamageB * multiplierB);
    }

    private float getElementMultiplier(String attackerElement, String defenderElement) {
        if (attackerElement.equals("Water") && defenderElement.equals("Fire")) {
            return 2.0f;  // Effective
        } else if (attackerElement.equals("Fire") && defenderElement.equals("Water")) {
            return 0.5f;  // Not effective
        } else if (attackerElement.equals("Fire") && defenderElement.equals("Normal")) {
            return 2.0f;  // Effective
        } else if (attackerElement.equals("Normal") && defenderElement.equals("Fire")) {
            return 0.5f;  // Not effective
        } else if (attackerElement.equals("Normal") && defenderElement.equals("Water")) {
            return 2.0f;  // Effective
        } else if (attackerElement.equals("Water") && defenderElement.equals("Normal")) {
            return 0.5f;  // Not effective
        } else {
            return 1.0f;  // No effect
        }
    }

    private String determineRoundWinner(Card cardA, Card cardB) {
        if (cardA.getDamage() > cardB.getDamage()) {
            return playerA.getUsername() + " wins the round.";
        } else if (cardB.getDamage() > cardA.getDamage()) {
            return playerB.getUsername() + " wins the round.";
        } else {
            return "Round is a draw.";
        }
    }

    private void updateDecksAfterRound(List<Card> winnerDeck, List<Card> loserDeck, Card winnerCard, Card loserCard) {
        // Remove the loser's card from their deck and add it to the winner's deck
        loserDeck.remove(loserCard);
        winnerDeck.add(loserCard);

        // Update the owner ID of the transferred card
        loserCard.setOwnerId(winnerCard.getOwnerId());

        battleLog.addEntry("Card " + loserCard.getName() + " has been transferred to " + winnerCard.getOwnerId());
        System.out.println("Card " + loserCard.getName() + " has been transferred to " + winnerCard.getOwnerId());
    }

    private void finalizeBattle() {
        String result;
        if (deckA.isEmpty() && deckB.isEmpty()) {
            result = "Battle is a draw.";
            battleLog.addEntry(result);
            System.out.println(result);
            // No ELO change
        } else if (deckA.isEmpty()) {
            result = playerB.getUsername() + " wins the battle!";
            battleLog.addEntry(result);
            System.out.println(result);
            updateElo(playerB, playerA);
        } else if (deckB.isEmpty()) {
            result = playerA.getUsername() + " wins the battle!";
            battleLog.addEntry(result);
            System.out.println(result);
            updateElo(playerA, playerB);
        } else {
            result = "Battle ended after 100 rounds without a winner.";
            battleLog.addEntry(result);
            System.out.println(result);
            // No ELO change
        }

        // Update players in the database
        userService.updateUserElo(playerA);
        userService.updateUserElo(playerB);
    }

    private void updateElo(User winner, User loser) {
        int newWinnerElo = Math.max(0, winner.getElo() + 3);
        int newLoserElo = Math.max(0, loser.getElo() - 5);
        winner.setElo(newWinnerElo);
        loser.setElo(newLoserElo);
    }
}

