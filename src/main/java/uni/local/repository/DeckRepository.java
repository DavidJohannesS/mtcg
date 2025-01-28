package uni.local.repository;

import uni.local.models.Card;
import uni.local.utils.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckRepository {
    private static DeckRepository instance;

    private DeckRepository() {}

    public static synchronized DeckRepository getInstance() {
        if (instance == null) {
            instance = new DeckRepository();
        }
        return instance;
    }

    public List<Card> findDeckByUserId(int userId) {
        String sql = "SELECT c.* FROM cards c JOIN deck d ON c.id = d.card_id WHERE d.user_id = ?";
        List<Card> cards = new ArrayList<>();
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Card card = new Card();
                card.setId(UUID.fromString(rs.getString("id")));
                card.setName(Card.CardName.valueOf(rs.getString("name")));
                card.setDamage(rs.getFloat("damage"));
                card.setOwnerId(rs.getInt("owner_id"));
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

public boolean isCardInDeck(int userId, UUID cardId) {
    String sql = "SELECT 1 FROM deck WHERE user_id = ? AND card_id = ?";
    try (Connection conn = DbConnection.getInstance();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, userId);
        pstmt.setObject(2, cardId);
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public boolean updateDeck(int userId, List<UUID> cardIds) {
        String deleteDeckSql = "DELETE FROM deck WHERE user_id = ?";
        String insertDeckSql = "INSERT INTO deck (user_id, card_id) VALUES (?, ?)";
        try (Connection conn = DbConnection.getInstance()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(deleteDeckSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertDeckSql)) {
                for (UUID cardId : cardIds) {
                    pstmt.setInt(1, userId);
                    pstmt.setObject(2, cardId);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

