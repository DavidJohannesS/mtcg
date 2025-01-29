package uni.local.services;
import java.util.List;
import uni.local.models.Card;
import uni.local.models.Trade;
import uni.local.repository.CardRepository;
import uni.local.repository.DeckRepository;
import uni.local.repository.TradeRepository;

import java.util.UUID;

public class TradeService {
    private static TradeService instance;
    private final TradeRepository tradeRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    private TradeService() {
        this.tradeRepository = TradeRepository.getInstance();
        this.cardRepository = CardRepository.getInstance();
        this.deckRepository = DeckRepository.getInstance();
    }

    public static synchronized TradeService getInstance() {
        if (instance == null) {
            instance = new TradeService();
        }
        return instance;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    public boolean createTrade(Trade trade) {
    // Fetch the card to be traded
    Card cardToTrade = cardRepository.findById(trade.getCardToTrade());
    if (cardToTrade == null) {
        throw new IllegalArgumentException("Card to trade not found");
    }
    // Check if the card is in a deck
    if (cardToTrade.isInDeck()) {
        throw new IllegalArgumentException("Cannot create a trade with a card that is in a deck");
    }
    // Business logic validations
    return tradeRepository.save(trade);
}


    public boolean deleteTrade(UUID tradeId) {
        return tradeRepository.delete(tradeId);
    }

public boolean acceptTrade(UUID tradeId, UUID offeredCardId, int userId) {
    // Fetch the trade details
    Trade trade = tradeRepository.findById(tradeId);
    if (trade == null) {
        throw new IllegalArgumentException("Trade not found");
    }

    // Ensure the user is not trying to trade with themselves
    if (trade.getOwnerId() == userId) {
        throw new IllegalArgumentException("Cannot accept your own trade");
    }

    // Validate that the user owns the offered card
    Card userCard = cardRepository.findById(offeredCardId);
    if (userCard == null || userCard.getOwnerId() != userId) {
        throw new IllegalArgumentException("You do not own the offered card");
    }

    // Check if the offered card is in a deck
    if (userCard.isInDeck()) {
        throw new IllegalArgumentException("Cannot trade a card that is in your deck");
    }

    // Determine the type from the card name
    String cardName = userCard.getName().toString();
    String cardType = determineCardType(cardName);

    // Validate the offered card against trade requirements
    if (!cardType.equalsIgnoreCase(trade.getType())) {
        throw new IllegalArgumentException("Offered card does not match the required type");
    }
    if (userCard.getDamage() < trade.getMinimumDamage()) {
        throw new IllegalArgumentException("Offered card does not meet the minimum damage requirement");
    }

    // Fetch the owner's card
    Card ownerCard = cardRepository.findById(trade.getCardToTrade());
    if (ownerCard == null) {
        throw new IllegalArgumentException("Owner's card not found");
    }

    // Ensure the owner's card is not in a deck
    if (ownerCard.isInDeck()) {
        throw new IllegalArgumentException("Cannot trade a card that is in a deck");
    }

    // Perform the trade: swap ownership of the cards
    boolean tradeSuccessful = cardRepository.tradeCards(
            offeredCardId, userId,
            trade.getCardToTrade(), trade.getOwnerId()
    );

    if (tradeSuccessful) {
        // Remove the trade after successful completion
        tradeRepository.delete(tradeId);
        return true;
    } else {
        return false;
    }
}


// Utility method to determine card type from card name
private String determineCardType(String cardName) {
    if (cardName.contains("Goblin") || cardName.contains("Troll") || cardName.contains("Elf") ||
        cardName.contains("Knight") || cardName.contains("Dragon") || cardName.contains("Ork") ||
        cardName.contains("Kraken") || cardName.contains("Wizzard")) {
        return "monster";
    } else if (cardName.contains("Spell")) {
        return "spell";
    } else {
        return "unknown";
    }
}
}

