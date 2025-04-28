package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {

    public static User authenticate(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT user_id, username, full_name, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // In production, use hashed passwords and secure comparison
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String user = rs.getString("username");
                String fullName = rs.getString("full_name");
                String role = rs.getString("role");
                return new User(userId, user, fullName, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
