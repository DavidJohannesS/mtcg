package uni.local.repository;

import uni.local.models.Card;
import uni.local.models.Package;
import uni.local.utils.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepository {



    public boolean createPackage(Package newPackage) {
        String sqlPackage = "INSERT INTO packages (id) VALUES (DEFAULT) RETURNING id";
        try (Connection conn = DbConnection.getInstance()) {
            conn.setAutoCommit(false); 
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPackage)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int packageId = rs.getInt(1);

                    for (Card card : newPackage.getCards()) {
                        saveCard(card, packageId, conn);
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

    private void saveCard(Card card, int packageId, Connection conn) throws SQLException {
        String sqlCard = "INSERT INTO cards (id, name, damage, package_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCard)) {
            pstmt.setObject(1, card.getId());
            pstmt.setString(2, card.getName().name());
            pstmt.setFloat(3, card.getDamage());
            pstmt.setInt(4, packageId);
            pstmt.executeUpdate();
        }
    }

}

