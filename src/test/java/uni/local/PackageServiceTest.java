package uni.local.services;

import uni.local.models.Card;
import uni.local.models.Package;
import uni.local.models.User;
import uni.local.repository.PackageRepository;
import uni.local.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class PackageServiceTest {

    private PackageService packageService;

    private PackageRepository mockPackageRepository;
    private UserRepository mockUserRepository;

    @BeforeEach
    void setUp() throws Exception {
        // Create instance of PackageService
        packageService = PackageService.getInstance();

        // Create mock instances
        mockPackageRepository = mock(PackageRepository.class);
        mockUserRepository = mock(UserRepository.class);

        // Use reflection to set the private final fields in PackageService
        Field packageRepoField = PackageService.class.getDeclaredField("packageRepository");
        packageRepoField.setAccessible(true);
        packageRepoField.set(packageService, mockPackageRepository);

        Field userRepoField = PackageService.class.getDeclaredField("userRepository");
        userRepoField.setAccessible(true);
        userRepoField.set(packageService, mockUserRepository);
    }

    @Test
    void testCreatePackage_InvalidNumberOfCards() throws SQLException {
        // Prepare test data with invalid number of cards (less than 5)
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(UUID.randomUUID(), Card.CardName.FireGoblin, 10.0f, null, 0));
        }

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.createPackage(cards);
        });

        assertEquals("A package must contain exactly 5 cards.", exception.getMessage());

        // Verify that createPackage on repository is never called
        verify(mockPackageRepository, never()).createPackage(any(Package.class));
    }

    @Test
    void testCreatePackage_Success() throws SQLException {
        // Prepare test data with exactly 5 cards
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(new Card(UUID.randomUUID(), Card.CardName.WaterElf, 15.0f, null, 0));
        }

        // Mock the repository method
        when(mockPackageRepository.createPackage(any(Package.class))).thenReturn(true);

        // Call the method under test
        boolean result = packageService.createPackage(cards);

        // Assertions
        assertTrue(result);
        verify(mockPackageRepository, times(1)).createPackage(any(Package.class));
    }

    @Test
    void testCreatePackage_RepositoryReturnsFalse() throws SQLException {
        // Prepare test data with exactly 5 cards
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(new Card(UUID.randomUUID(), Card.CardName.WaterElf, 15.0f, null, 0));
        }

        // Mock the repository method to return false
        when(mockPackageRepository.createPackage(any(Package.class))).thenReturn(false);

        // Call the method under test
        boolean result = packageService.createPackage(cards);

        // Assertions
        assertFalse(result);
        verify(mockPackageRepository, times(1)).createPackage(any(Package.class));
    }

    @Test
    void testBuyPackage_UserHasInsufficientCoins() throws SQLException {
        // Prepare test data
        User user = new User(1, "testuser", "password", 3, 100); // User has only 3 coins

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.buyPackage(user);
        });

        assertEquals("Insufficient coins", exception.getMessage());

        // Verify that repositories are not called
        verify(mockPackageRepository, never()).getRandomPackage(any());
        verify(mockUserRepository, never()).updateUserCoins(anyInt(), anyInt(), any());
    }
}

