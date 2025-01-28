package uni.local.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uni.local.models.Card;
import uni.local.repository.CardRepository;
import uni.local.repository.DeckRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeckServiceTest {

    private DeckService deckService;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private CardRepository cardRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject mocks into DeckService
        deckService = new DeckService(deckRepository, cardRepository);
    }

    @Test
    public void testGetDeck_Success() {
        int userId = 1;
        List<Card> mockDeck = new ArrayList<>();
        mockDeck.add(new Card(UUID.randomUUID(), Card.CardName.FireGoblin, 50.0f, null, userId));
        mockDeck.add(new Card(UUID.randomUUID(), Card.CardName.WaterElf, 60.0f, null, userId));

        when(deckRepository.findDeckByUserId(userId)).thenReturn(mockDeck);

        List<Card> result = deckService.getDeck(userId);

        assertNotNull(result, "Deck should not be null");
        assertEquals(2, result.size(), "Deck should contain 2 cards");
        verify(deckRepository, times(1)).findDeckByUserId(userId);
    }

    @Test
    public void testSetDeck_Success() {
        int userId = 1;
        List<String> cardIds = Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        when(deckRepository.updateDeck(eq(userId), anyList())).thenReturn(true);

        boolean result = deckService.setDeck(userId, cardIds);

        assertTrue(result, "Deck should be set successfully");
        verify(deckRepository, times(1)).updateDeck(eq(userId), anyList());
    }

    @Test
    public void testSetDeck_InvalidCardCount() {
        int userId = 1;
        List<String> cardIds = Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        ); // Only 2 cards instead of 4

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deckService.setDeck(userId, cardIds);
        });

        String expectedMessage = "A deck must contain exactly 4 cards.";
        assertEquals(expectedMessage, exception.getMessage());
        verify(deckRepository, never()).updateDeck(anyInt(), anyList());
    }

    @Test
    public void testSetRandomDeck_Success() {
        int userId = 1;
        List<Card> userCards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userCards.add(new Card(UUID.randomUUID(), Card.CardName.Wizard, 70.0f, null, userId));
        }

        when(cardRepository.findByUserId(userId)).thenReturn(userCards);
        when(deckRepository.updateDeck(eq(userId), anyList())).thenReturn(true);

        boolean result = deckService.setRandomDeck(userId);

        assertTrue(result, "Random deck should be set successfully");
        verify(cardRepository, times(1)).findByUserId(userId);
        verify(deckRepository, times(1)).updateDeck(eq(userId), anyList());
    }

    @Test
    public void testSetRandomDeck_NotEnoughCards() {
        int userId = 1;
        List<Card> userCards = new ArrayList<>();
        userCards.add(new Card(UUID.randomUUID(), Card.CardName.Knight, 30.0f, null, userId));

        when(cardRepository.findByUserId(userId)).thenReturn(userCards);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deckService.setRandomDeck(userId);
        });

        String expectedMessage = "User does not have enough cards to form a deck.";
        assertEquals(expectedMessage, exception.getMessage());
        verify(cardRepository, times(1)).findByUserId(userId);
        verify(deckRepository, never()).updateDeck(anyInt(), anyList());
    }
}

