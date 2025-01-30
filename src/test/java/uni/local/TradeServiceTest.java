import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uni.local.models.Card;
import uni.local.models.Trade;
import uni.local.repository.CardRepository;
import uni.local.repository.DeckRepository;
import uni.local.repository.TradeRepository;
import uni.local.services.TradeService;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    // Mocks for repositories
    private TradeRepository tradeRepository;
    private CardRepository cardRepository;
    private DeckRepository deckRepository;

    // MockedStatic variables
    private MockedStatic<TradeRepository> mockedTradeRepo;
    private MockedStatic<CardRepository> mockedCardRepo;
    private MockedStatic<DeckRepository> mockedDeckRepo;

    private TradeService tradeService;

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton instance of TradeService before each test
        Field instanceField = TradeService.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Create mocks for repositories
        tradeRepository = mock(TradeRepository.class);
        cardRepository = mock(CardRepository.class);
        deckRepository = mock(DeckRepository.class);

        // Mock the static getInstance() methods
        mockedTradeRepo = Mockito.mockStatic(TradeRepository.class);
        mockedCardRepo = Mockito.mockStatic(CardRepository.class);
        mockedDeckRepo = Mockito.mockStatic(DeckRepository.class);

        mockedTradeRepo.when(TradeRepository::getInstance).thenReturn(tradeRepository);
        mockedCardRepo.when(CardRepository::getInstance).thenReturn(cardRepository);
        mockedDeckRepo.when(DeckRepository::getInstance).thenReturn(deckRepository);

        // Initialize the TradeService singleton instance
        tradeService = TradeService.getInstance();
    }

    @AfterEach
    void tearDown() {
        // Close the mocked static instances to avoid interference with other tests
        mockedTradeRepo.close();
        mockedCardRepo.close();
        mockedDeckRepo.close();
    }

    @Test
    void createTrade_CardNotFound_ThrowsException() {
        // Arrange
        UUID cardId = UUID.randomUUID();
        Trade trade = new Trade();
        trade.setCardToTrade(cardId);

        when(cardRepository.findById(cardId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.createTrade(trade);
        });

        assertEquals("Card to trade not found", exception.getMessage());
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
        card.setInDeck(true);

        when(cardRepository.findById(cardId)).thenReturn(card);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.createTrade(trade);
        });

        assertEquals("Cannot create a trade with a card that is in a deck", exception.getMessage());
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void createTrade_SuccessfulCreation_ReturnsTrue() {
        // Arrange
        UUID cardId = UUID.randomUUID();
        Trade trade = new Trade();
        trade.setCardToTrade(cardId);

        Card card = new Card();
        card.setId(cardId);
        card.setInDeck(false);

        when(cardRepository.findById(cardId)).thenReturn(card);
        when(tradeRepository.save(trade)).thenReturn(true);

        // Act
        boolean result = tradeService.createTrade(trade);

        // Assert
        assertTrue(result);
        verify(tradeRepository, times(1)).save(trade);
    }

}

