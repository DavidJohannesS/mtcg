package uni.local.repository;

import uni.local.models.Card;
import uni.local.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardRepository {
    private static CardRepository instance;

    private CardRepository() {}

    public static synchronized CardRepository getInstance() {
        if (instance == null) {
            instance = new CardRepository();
        }
        return instance;
    }

    public List<Card> getCardsForPackage(int packageId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM cards WHERE package_id = ?";
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, packageId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                UUID cardId = (UUID) rs.getObject("id");
                Card.CardName name = Card.CardName.valueOf(rs.getString("name"));
                float damage = rs.getFloat("damage");
                int ownerId = rs.getInt("owner_id");
                cards.add(new Card(cardId, name, damage, packageId, ownerId));
            }
        }
        return cards;
    }

    public void updateCardOwnership(UUID cardId, int userId, Connection conn) throws SQLException {
        String sql = "UPDATE cards SET owner_id = ?, package_id = NULL WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setObject(2, cardId);
            pstmt.executeUpdate();
        }
    }

    public void addCard(Card card, int packageId, Connection conn) throws SQLException {
        String sql = "INSERT INTO cards (id, name, damage, package_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, card.getId());
            pstmt.setString(2, card.getName().name());
            pstmt.setFloat(3, card.getDamage());
            pstmt.setInt(4, packageId);
            pstmt.executeUpdate();
        }
    }
    public List<Card> findByUserId(int userId)
    {
        String sql = "SELECT * FROM cards WHERE owner_id = ?";
        List<Card> cards = new ArrayList<>();
        try(Connection conn = DbConnection.getInstance();
                PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1,userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                Card card = new Card();
                card.setId(UUID.fromString(rs.getString("id")));
                card.setName(Card.CardName.valueOf(rs.getString("name")));
                card.setDamage(rs.getFloat("damage"));
                card.setOwnerId(rs.getInt("owner_id"));
                card.setPackageId(rs.getInt("package_id"));
                cards.add(card);
            }
        } catch (SQLException e)
        { e.printStackTrace();
        }
        return cards;
    }



    public Card findById(UUID cardId) {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, cardId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Card card = new Card();
                card.setId(UUID.fromString(rs.getString("id")));
                card.setName(Card.CardName.valueOf(rs.getString("name")));
                card.setDamage(rs.getFloat("damage"));
                card.setOwnerId(rs.getInt("ownerId"));
                return card;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean tradeCards(UUID cardIdUser, int userId, UUID cardIdOwner, int ownerId) {
        String sqlUpdateCardUser = "UPDATE cards SET ownerId = ? WHERE id = ?";
        String sqlUpdateCardOwner = "UPDATE cards SET ownerId = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUser = conn.prepareStatement(sqlUpdateCardUser);
                    PreparedStatement pstmtOwner = conn.prepareStatement(sqlUpdateCardOwner)) {

                // Transfer user's card to owner
                pstmtUser.setInt(1, ownerId);
                pstmtUser.setObject(2, cardIdUser);
                pstmtUser.executeUpdate();

                // Transfer owner's card to user
                pstmtOwner.setInt(1, userId);
                pstmtOwner.setObject(2, cardIdOwner);
                pstmtOwner.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

