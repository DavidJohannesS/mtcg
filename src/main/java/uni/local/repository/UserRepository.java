package uni.local.repository;

import uni.local.models.User;
import uni.local.utils.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository
{
    public void save ( User user ) throws SQLException
    {
        String sql = "INSERT INTO users (username, password,coins) VALUES (?, ?, ?)";
        try ( Connection conn = DbConnection.getInstance();
              PreparedStatement pstmt = conn.prepareStatement( sql ) )
        {
            pstmt.setString( 1, user.getUsername() );
            pstmt.setString( 2, user.getPassword() );
            pstmt.setInt( 3, user.getCoins() );
            pstmt.executeUpdate();
        }
    }

    public boolean existsByUsername ( String username ) throws SQLException
    {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try ( Connection conn = DbConnection.getInstance();
              PreparedStatement pstmt = conn.prepareStatement( sql ) )
        {
            pstmt.setString( 1, username );
            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    return rs.getInt( 1 ) > 0;
                }
            }
        }
        return false;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String password = rs.getString("password");
                    int coins = rs.getInt("coins");
                    int elo = rs.getInt("elo");
                    // Use the constructor that includes id
                    return new User(id, username, password, coins, elo);
                }
            }
        }
        return null;
    }


    public void updateUserCoins(int userId, int newCoinCount, Connection conn) throws SQLException
    {
        String sql = "UPDATE users SET coins = ? WHERE id = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, newCoinCount);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, coins = ?, elo = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, user.getCoins());
            pstmt.setInt(4, user.getElo());
            pstmt.setInt(5, user.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public User findById(int userId) {
    String sql = "SELECT * FROM users WHERE id = ?";
    try (Connection conn = DbConnection.getInstance();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new User(
                rs.getInt("id"), // Ensure the ID is set
                rs.getString("username"),
                rs.getString("password"),
                rs.getInt("coins"),
                rs.getInt("elo")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
public void updateUserElo(int userId, int elo) {
    String sql = "UPDATE users SET elo = ? WHERE id = ?";
    try (Connection conn = DbConnection.getInstance();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, elo);
        pstmt.setInt(2, userId);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public void deleteByUsername(String username) {
    String sql = "DELETE FROM users WHERE username = ?";
    try (Connection conn = DbConnection.getInstance();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, username);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



}

