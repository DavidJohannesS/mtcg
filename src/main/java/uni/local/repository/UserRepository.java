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

    public User findByUsername ( String username ) throws SQLException
    {
        String sql = "SELECT * FROM users WHERE username = ?";
        try ( Connection conn = DbConnection.getInstance();
              PreparedStatement pstmt = conn.prepareStatement( sql ) )
        {
            pstmt.setString( 1, username );
            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    return new User( rs.getString( "username" ), rs.getString( "password" ), rs.getInt( "coins" ) );
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
                rs.getInt("coins")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

}

