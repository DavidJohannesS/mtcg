package uni.local.services;
import uni.local.utils.*;
import uni.local.models.User;
import uni.local.models.Package;
import uni.local.models.Card;
import uni.local.repository.PackageRepository;
import uni.local.repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PackageService {
    private static PackageService instance;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

    private PackageService() {
        this.packageRepository = new PackageRepository();
        this.userRepository = new UserRepository();
    }

    public static synchronized PackageService getInstance() {
        if (instance == null) {
            instance = new PackageService();
        }
        return instance;
    }

    public boolean createPackage(List<Card> cards) throws SQLException {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A package must contain exactly 5 cards.");
        }
        Package cardPackage = new Package(cards);
        return packageRepository.createPackage(cardPackage);
    }

    public boolean buyPackage(User user) throws SQLException {
        if (user.getCoins() < 5) {
            throw new IllegalArgumentException("Insufficient coins");
        }

        try (Connection conn = DbConnection.getInstance()) {
            conn.setAutoCommit(false);
            Package selectedPackage = packageRepository.getRandomPackage(conn);
            for (Card card : selectedPackage.getCards()) {
                packageRepository.getCardRepository().updateCardOwnership(card.getId(), user.getId(), conn);
            }

            packageRepository.removePackage(selectedPackage.getId(), conn);
            userRepository.updateUserCoins(user.getId(), user.getCoins() - 5, conn);

            conn.commit();
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new SQLException("Failed to buy package", e);
        }
    }
}

