package uni.local.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uni.local.models.Card;
import uni.local.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class CardServiceTest {

    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cardService = new CardService(cardRepository);
    }

    @Test
    public void testGetUserCards_WithCards() {
        int userId = 1;

        List<Card> mockCards = new ArrayList<>();
        mockCards.add(new Card(UUID.randomUUID(), Card.CardName.FireGoblin, 50.0f, null, userId));
        mockCards.add(new Card(UUID.randomUUID(), Card.CardName.WaterElf, 60.0f, null, userId));

        when(cardRepository.findByUserId(userId)).thenReturn(mockCards);

        List<Card> result = cardService.getUserCards(userId);

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return 2 cards");
        verify(cardRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetUserCards_NoCards() {
        int userId = 2;

        when(cardRepository.findByUserId(userId)).thenReturn(new ArrayList<>());

        List<Card> result = cardService.getUserCards(userId);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return an empty list");
        verify(cardRepository, times(1)).findByUserId(userId);
    }
    @Test
public void testGetUserCards_InvalidUserId() {
    int invalidUserId = -1;

    when(cardRepository.findByUserId(invalidUserId)).thenReturn(new ArrayList<>());

    List<Card> result = cardService.getUserCards(invalidUserId);

    assertNotNull(result, "Result should not be null");
    assertTrue(result.isEmpty(), "Should return an empty list for invalid user ID");
    verify(cardRepository, times(1)).findByUserId(invalidUserId);
}
@Test
public void testGetUserCards_RepositoryException() {
    int userId = 1;

    when(cardRepository.findByUserId(userId)).thenThrow(new RuntimeException("Database error"));

    Exception exception = assertThrows(RuntimeException.class, () -> {
        cardService.getUserCards(userId);
    });

    assertEquals("Database error", exception.getMessage());
    verify(cardRepository, times(1)).findByUserId(userId);
}

}

