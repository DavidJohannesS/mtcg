package uni.local.services;

import uni.local.models.Card;
import uni.local.repository.CardRepository;
import uni.local.repository.DeckRepository;

import java.util.List;
import java.util.UUID;

public class DeckService {
    private static DeckService instance;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    private DeckService() {
        this.deckRepository = DeckRepository.getInstance();
        this.cardRepository = CardRepository.getInstance();
    }

    public static synchronized DeckService getInstance() {
        if (instance == null) {
            instance = new DeckService();
        }
        return instance;
    }

    public List<Card> getDeck(int userId) {
        return deckRepository.findDeckByUserId(userId);
    }

    public boolean setDeck(int userId, List<String> cardIds) {
        if (cardIds.size() != 4) {
            throw new IllegalArgumentException("A deck must contain exactly 4 cards.");
        }
        List<UUID> cardUUIDs = cardIds.stream().map(UUID::fromString).toList();
        return deckRepository.updateDeck(userId, cardUUIDs);
    }
}

