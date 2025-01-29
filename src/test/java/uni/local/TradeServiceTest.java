import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uni.local.models.Card;
import uni.local.models.Trade;
import uni.local.repository.CardRepository;
import uni.local.repository.DeckRepository;
import uni.local.repository.TradeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import uni.local.services.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeServiceTest {


    @InjectMocks

    private TradeService tradeService;


    @Mock

    private TradeRepository tradeRepository;


    @Mock

    private CardRepository cardRepository;


    @Mock

    private DeckRepository deckRepository;


    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

    }


    @Test

    void createTrade_CardNotFound_ThrowsException() {

        // Arrange

        UUID cardId = UUID.randomUUID();

        Trade trade = new Trade();

        trade.setCardToTrade(cardId);

        when(cardRepository.findById(cardId)).thenReturn(null);


        // Act & Assert

        assertThrows(IllegalArgumentException.class, () -> tradeService.createTrade(trade));

        verify(tradeRepository, never()).save(any(Trade.class));

    }

    @Test

    void createTrade_CardInDeck_ThrowsException() {

        // Arrange

        UUID cardId = UUID.randomUUID();

        Trade trade = new Trade();

        trade.setCardToTrade(cardId);

        Card card = new Card();

        card.setId(cardId);

        card.setInDeck(true); // Set the card to be in a deck

        when(cardRepository.findById(cardId)).thenReturn(card);


        // Act & Assert

        assertThrows(IllegalArgumentException.class, () -> tradeService.createTrade(trade));

        verify(tradeRepository, never()).save(any(Trade.class));

    }
    @Test
    void createTrade_CardDoesNotBelongToUser_ThrowsException() {
        // Arrange
        UUID cardId = UUID.randomUUID();
        int ownerId = 1;
        int otherUserId = 2;

        Trade trade = new Trade();
        trade.setCardToTrade(cardId);
        trade.setOwnerId(otherUserId); // Trade owner is different from card owner

        Card card = new Card();
        card.setId(cardId);
        card.setOwnerId(ownerId); // Card belongs to ownerId
        card.setInDeck(false); // Ensure it passes the deck check

        when(cardRepository.findById(cardId)).thenReturn(card);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tradeService.createTrade(trade));
        verify(tradeRepository, never()).save(any(Trade.class));
    }

}
