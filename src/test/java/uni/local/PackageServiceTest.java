package uni.local.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uni.local.models.Card;
import uni.local.models.Package;
import uni.local.models.User;
import uni.local.repository.PackageRepository;
import uni.local.repository.UserRepository;
import uni.local.repository.CardRepository;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PackageServiceTest {

    private PackageService packageService;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        packageService = PackageService.getInstance();

        // Use reflection to set the private repository fields
        Field packageRepoField = PackageService.class.getDeclaredField("packageRepository");
        packageRepoField.setAccessible(true);
        packageRepoField.set(packageService, packageRepository);

        Field userRepoField = PackageService.class.getDeclaredField("userRepository");
        userRepoField.setAccessible(true);
        userRepoField.set(packageService, userRepository);
    }

    @Test
    public void testCreatePackage_Success() throws SQLException {
        // Prepare 5 cards
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Card card = new Card(UUID.randomUUID(), Card.CardName.FireGoblin, 10.0f, null, 0);
            cards.add(card);
        }

        when(packageRepository.createPackage(any(Package.class))).thenReturn(true);

        boolean result = packageService.createPackage(cards);

        assertTrue(result, "Package should be created successfully.");
        verify(packageRepository, times(1)).createPackage(any(Package.class));
    }

    @Test
    public void testCreatePackage_InvalidNumberOfCards() {
        // Prepare only 3 cards
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Card card = new Card(UUID.randomUUID(), Card.CardName.WaterElf, 15.0f, null, 0);
            cards.add(card);
        }

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.createPackage(cards);
        });

        String expectedMessage = "A package must contain exactly 5 cards.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verify(packageRepository, never()).createPackage(any(Package.class));
    }

    @Test
    public void testBuyPackage_Success() throws SQLException {
        User user = new User(1, "testuser", "password", 10, 100);

        // Mock package
        Package mockPackage = new Package();
        mockPackage.setId(1);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Card card = new Card(UUID.randomUUID(), Card.CardName.RegularSpell, 20.0f, null, 0);
            cards.add(card);
        }
        mockPackage.setCards(cards);

        // Mock the dependencies
        when(packageRepository.getRandomPackage(any(Connection.class))).thenReturn(mockPackage);
        doNothing().when(packageRepository).removePackage(eq(1), any(Connection.class));
        when(packageRepository.getCardRepository()).thenReturn(mock(CardRepository.class));
        doNothing().when(userRepository).updateUserCoins(eq(user.getId()), anyInt(), any(Connection.class));

        boolean result = packageService.buyPackage(user);

        assertTrue(result, "User should be able to buy a package successfully.");
        verify(packageRepository, times(1)).getRandomPackage(any(Connection.class));
        verify(packageRepository, times(1)).removePackage(eq(1), any(Connection.class));
        verify(userRepository, times(1)).updateUserCoins(eq(user.getId()), eq(user.getCoins() - 5), any(Connection.class));
    }

    @Test
    public void testBuyPackage_InsufficientCoins() throws SQLException {
        User user = new User(1, "testuser", "password", 3, 100); // User has only 3 coins

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.buyPackage(user);
        });

        String expectedMessage = "Insufficient coins";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verify(packageRepository, never()).getRandomPackage(any(Connection.class));
        verify(userRepository, never()).updateUserCoins(anyInt(), anyInt(), any(Connection.class));
    }
}

