package uni.local.repository;

import uni.local.models.Trade;
import uni.local.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeRepository {
    private static TradeRepository instance;

    private TradeRepository() {}

    public static synchronized TradeRepository getInstance() {
        if (instance == null) {
            instance = new TradeRepository();
        }
        return instance;
    }

    public List<Trade> findAll() {
        String sql = "SELECT * FROM trades";
        List<Trade> trades = new ArrayList<>();
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Trade trade = new Trade();
                trade.setId(UUID.fromString(rs.getString("id")));
                trade.setCardToTrade(UUID.fromString(rs.getString("CardToTrade")));
                trade.setType(rs.getString("Type"));
                trade.setMinimumDamage(rs.getFloat("MinimumDamage"));
                trade.setOwnerId(rs.getInt("owner_id"));
                trades.add(trade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trades;
    }

    public boolean save(Trade trade) {
        String sql = "INSERT INTO trades (id, CardToTrade, Type, MinimumDamage, owner_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, trade.getId());
            pstmt.setObject(2, trade.getCardToTrade()); 
            pstmt.setString(3, trade.getType());
            pstmt.setFloat(4, trade.getMinimumDamage());
            pstmt.setInt(5, trade.getOwnerId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(UUID tradeId, int userId) {
        String sql = "DELETE FROM trades WHERE id = ? AND owner_id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, tradeId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

