package uni.local.services;

import uni.local.models.Card;
import uni.local.repository.CardRepository;

import java.util.List;

public class CardService {
    private static CardService instance;
    private final CardRepository cardRepository;

    private CardService() {
        this.cardRepository = CardRepository.getInstance();
    }

    public static synchronized CardService getInstance() {
        if (instance == null) {
            instance = new CardService();
        }
        return instance;
    }

    public List<Card> getUserCards(int userId) {
        return cardRepository.findByUserId(userId);
    }
}

