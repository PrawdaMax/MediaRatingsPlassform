package org.example.pkgServer.pkgRepositories;

import org.example.pkgDB.Database;

import java.sql.*;
import java.util.*;

public class TokenRepository {

    public void saveToken(String token, UUID userId) {
        String sql = "INSERT INTO tokens (token, user_id) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public String getTokenByUserId(UUID userId) {
        String sql = "SELECT token FROM tokens WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("token") : "";
        } catch (SQLException e) { e.printStackTrace(); return ""; }
    }

    public void deleteToken(String token) {
        String sql = "DELETE FROM tokens WHERE token = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}