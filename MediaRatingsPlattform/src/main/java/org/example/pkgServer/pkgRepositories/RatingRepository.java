package org.example.pkgServer.pkgRepositories;

import org.example.pkgDB.Database;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.Like;
import java.sql.*;
import java.util.*;

public class RatingRepository {

    public void addRating(Rating r) {
        String sql = "INSERT INTO ratings (id, value, comment, timestamp, confirmed, user_id, media_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, r.getId());
            stmt.setInt(2, r.getValue());
            stmt.setString(3, r.getComment());
            stmt.setTimestamp(4, Timestamp.valueOf(r.getTimestamp()));
            stmt.setBoolean(5, r.isConfirmed());
            stmt.setObject(6, r.getUserId());
            stmt.setObject(7, r.getMediaId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void addLike(Like like) {
        String sql = "INSERT INTO likes (id, user_id, rating_id) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID()); // Ensure ID is set
            stmt.setObject(2, like.getUserId());
            stmt.setObject(3, like.getRatingId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Rating> getRatingList() {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Rating r = new Rating(rs.getInt("value"), rs.getString("comment"),
                        rs.getTimestamp("timestamp").toString(),
                        UUID.fromString(rs.getString("user_id")),
                        UUID.fromString(rs.getString("media_id")));
                r.setId(UUID.fromString(rs.getString("id")));
                r.setConfirmed(rs.getBoolean("confirmed"));
                ratings.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ratings;
    }

    public List<Rating> getRatingsOfUser(UUID userId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rating r = new Rating(rs.getInt("value"), rs.getString("comment"),
                            rs.getTimestamp("timestamp").toString(),
                            UUID.fromString(rs.getString("user_id")),
                            UUID.fromString(rs.getString("media_id")));
                    r.setId(UUID.fromString(rs.getString("id")));
                    ratings.add(r);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ratings;
    }

    public List<Like> getLikes() {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM likes";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Like like = new Like(UUID.fromString(rs.getString("user_id")),
                        UUID.fromString(rs.getString("rating_id")));
                like.setId(UUID.fromString(rs.getString("id")));
                likes.add(like);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return likes;
    }

    public void deleteLike(UUID likeId) {
        String sql = "DELETE FROM likes WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, likeId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Rating updateRating(Map<String, Object> updates, Rating rating) {
        if (updates == null || updates.isEmpty() || rating == null) {
            return rating;
        }

        StringBuilder sql = new StringBuilder("UPDATE ratings SET ");
        List<Object> values = new ArrayList<>();

        for (String key : updates.keySet()) {
            sql.append(key).append(" = ?, ");
            values.add(updates.get(key));
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < values.size(); i++) {
                Object value = values.get(i);

                if (value instanceof Number) {
                    stmt.setInt(i + 1, ((Number) value).intValue());
                } else {
                    stmt.setObject(i + 1, value);
                }
            }

            stmt.setObject(values.size() + 1, rating.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                applyChangesToRatingObject(rating, updates);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rating;
    }

    private void applyChangesToRatingObject(Rating rating, Map<String, Object> updates) {
        if (updates.containsKey("stars")) {
            rating.setValue(((Number) updates.get("stars")).intValue());
        }

        if (updates.containsKey("comment")) {
            rating.setComment((String) updates.get("comment"));
        }
    }
}