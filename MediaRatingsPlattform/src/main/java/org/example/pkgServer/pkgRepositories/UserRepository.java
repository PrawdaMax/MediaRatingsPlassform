package org.example.pkgServer.pkgRepositories;

import org.example.pkgDB.Database;
import org.example.pkgObj.User;
import java.sql.*;
import java.util.*;

public class UserRepository {

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User u = new User(rs.getString("username"), rs.getString("password"));
                u.setId(UUID.fromString(rs.getString("id")));
                users.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public boolean addUser(User user) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, user.getUsername());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) return false;
            }
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setObject(1, user.getId());
                insertStmt.setString(2, user.getUsername());
                insertStmt.setString(3, user.getPassword());
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User(rs.getString("username"), rs.getString("password"));
                u.setId(UUID.fromString(rs.getString("id")));
                return u;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<User> getUserList() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User u = new User(rs.getString("username"), rs.getString("password"));
                u.setId(UUID.fromString(rs.getString("id")));
                users.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public boolean findUser(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public User getUser(UUID uuid) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User(rs.getString("username"), rs.getString("password"));
                u.setId(UUID.fromString(rs.getString("id")));
                return u;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void deleteUser(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}