package uni.local.repository;

import uni.local.models.Card;
import uni.local.models.Package;
import uni.local.utils.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PackageRepository {
    private final CardRepository cardRepository = CardRepository.getInstance();

    public CardRepository getCardRepository() {
        return cardRepository;
    }

    public boolean createPackage(Package newPackage) {
        String sqlPackage = "INSERT INTO packages (id) VALUES (DEFAULT) RETURNING id";
        try (Connection conn = DbConnection.getInstance()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPackage)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int packageId = rs.getInt(1);

                    for (Card card : newPackage.getCards()) {
                        cardRepository.addCard(card, packageId, conn);
                    }

                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    System.err.println("Failed to create package.");
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                System.err.println("Failed to create package. Error: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Package getRandomPackage(Connection conn) throws SQLException {
        String sql = "SELECT * FROM packages ORDER BY RANDOM() LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int packageId = rs.getInt("id");
                List<Card> cards = cardRepository.getCardsForPackage(packageId, conn);
                return new Package(packageId, cards);
            }
            throw new SQLException("No packages available");
        }
    }

    public void removePackage(int packageId, Connection conn) throws SQLException {
        String sql = "DELETE FROM packages WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, packageId);
            pstmt.executeUpdate();
        }
    }
}

