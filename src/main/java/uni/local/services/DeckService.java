package uni.local.services;

import uni.local.models.Card;
import uni.local.repository.CardRepository;
import uni.local.repository.DeckRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeckService {
    private static DeckService instance;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    // Default constructor for production code
    private DeckService() {
        this.deckRepository = DeckRepository.getInstance();
        this.cardRepository = CardRepository.getInstance();
    }

    // Constructor for dependency injection (testing)
    public DeckService(DeckRepository deckRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
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
        List<UUID> cardUUIDs = cardIds.stream().map(UUID::fromString).collect(Collectors.toList());
        return deckRepository.updateDeck(userId, cardUUIDs);
    }

    public boolean setRandomDeck(int userId) {
        List<Card> userCards = cardRepository.findByUserId(userId);
        if (userCards.size() < 4) {
            throw new IllegalArgumentException("User does not have enough cards to form a deck.");
        }
        Collections.shuffle(userCards);
        List<UUID> randomCardIds = userCards.stream()
                .limit(4)
                .map(Card::getId)
                .collect(Collectors.toList());
        return deckRepository.updateDeck(userId, randomCardIds);
    }
}

