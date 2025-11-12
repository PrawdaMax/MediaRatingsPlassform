package org.example.pkgDB;

import org.example.pkgMisc.MediaType;
import org.example.pkgObj.*;
import org.example.pkgServer.pkgToken.JWTUtil;

import java.sql.*;
import java.util.*;

/**
 * Database class backed by PostgreSQL using pure java.sql (JDBC)
 */
public class Database {

    private final String url = "jdbc:postgresql://localhost:5432/mediaRatingsDB";
    private final String user = "admin";
    private final String password = "admin";

    public Database() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC driver not found", e);
        }
    }

    /* -------------------- Utility -------------------- */

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /* -------------------- Basic Operations -------------------- */

    public String getAllData() {
        StringBuilder sb = new StringBuilder();

        String sqlUsers = "SELECT * FROM users";
        String sqlMedia = "SELECT * FROM media";
        String sqlRatings = "SELECT * FROM ratings";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // ---- Users ----
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlUsers)) {

                sb.append("=== USERS ===\n");
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getString("id"))
                            .append(", Username: ").append(rs.getString("username"))
                            .append(", Password: ").append(rs.getString("password"))
                            .append("\n");
                }
            }

            // ---- Media ----
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlMedia)) {

                sb.append("\n=== MEDIA ===\n");
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getString("id"))
                            .append(", Title: ").append(rs.getString("title"))
                            .append(", Description: ").append(rs.getString("description"))
                            .append(", Type: ").append(rs.getString("media_type"))
                            .append(", Year: ").append(rs.getInt("year"))
                            .append(", AgeRestriction: ").append(rs.getInt("age_restriction"))
                            .append("\n");
                }
            }

            // ---- Ratings ----
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlRatings)) {

                sb.append("\n=== RATINGS ===\n");
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getString("id"))
                            .append(", Value: ").append(rs.getInt("value"))
                            .append(", Comment: ").append(rs.getString("comment"))
                            .append(", Timestamp: ").append(rs.getTimestamp("timestamp"))
                            .append(", Confirmed: ").append(rs.getBoolean("confirmed"))
                            .append(", UserID: ").append(rs.getString("user_id"))
                            .append(", MediaID: ").append(rs.getString("media_id"))
                            .append("\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /* -------------------- User Operations -------------------- */

    public List<User> getUserList() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User u = new User(
                        rs.getString("username"),
                        rs.getString("password")
                );
                u.setId(UUID.fromString(rs.getString("id")));
                users.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean addUser(User userObj) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        String insertSql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, userObj.getUsername());
            checkStmt.setString(2, userObj.getPassword());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return false; // user already exists
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setObject(1, userObj.getId());
                insertStmt.setString(2, userObj.getUsername());
                insertStmt.setString(3, userObj.getPassword());
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean findUser(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUser(UUID uuid) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* -------------------- Media Operations -------------------- */

    public List<Media> getMediaList() {
        List<Media> mediaList = new ArrayList<>();
        String sql = "SELECT * FROM media";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Media m = new Media(
                        rs.getString("title"),
                        rs.getString("description"),
                        MediaType.valueOf(rs.getString("media_type")),
                        rs.getInt("year"),
                        new ArrayList<>(),  // Genres werden hier leer gelassen, falls du sie separat lädst
                        rs.getInt("age_restriction")
                );
                m.setId(UUID.fromString(rs.getString("id")));
                mediaList.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mediaList;
    }


    public void addMedia(Media media) {
        String sql = """
            INSERT INTO media (id, title, description, media_type, year, age_restriction)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, media.getId());
            stmt.setString(2, media.getTitle());
            stmt.setString(3, media.getDescription());
            stmt.setObject(4, media.getMediaType(), java.sql.Types.OTHER);
            stmt.setInt(5, media.getYear());
            stmt.setInt(6, media.getAgeRestriction());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMedia(UUID uuid) {
        String sql = "DELETE FROM media WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getMediaNameList() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT title FROM media";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* -------------------- Rating Operations -------------------- */

    public List<Rating> getRatingList() {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Rating r = new Rating(
                        rs.getInt("value"),
                        rs.getString("comment"),
                        rs.getTimestamp("timestamp").toString(),
                        UUID.fromString(rs.getString("user_id")),
                        UUID.fromString(rs.getString("media_id"))
                );
                r.setId(UUID.fromString(rs.getString("id")));
                r.setConfirmed(rs.getBoolean("confirmed"));
                ratings.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public void addRating(Rating rating) {
        String sql = """
            INSERT INTO ratings (id, value, comment, timestamp, confirmed, user_id, media_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, rating.getId());
            stmt.setInt(2, rating.getValue());
            stmt.setString(3, rating.getComment());
            stmt.setTimestamp(4, Timestamp.valueOf(rating.getTimestamp()));
            stmt.setBoolean(5, rating.isConfirmed());
            stmt.setObject(6, rating.getUserId());
            stmt.setObject(7, rating.getMediaId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Rating> getRatingsOfUser(UUID userId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE user_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rating rating = new Rating(
                        rs.getInt("value"),
                        rs.getString("comment"),
                        rs.getTimestamp("timestamp").toString(),
                        UUID.fromString(rs.getString("user_id")),
                        UUID.fromString(rs.getString("media_id"))
                );
                rating.setId(UUID.fromString(rs.getString("id")));
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    /* -------------------- Like Operations -------------------- */

    public List<Like> getLikes() {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM likes";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Like like = new Like(
                        UUID.fromString(rs.getString("user_id")),
                        UUID.fromString(rs.getString("rating_id"))
                );
                like.setId(UUID.fromString(rs.getString("id"))); // falls deine Like-Klasse setId() hat
                likes.add(like);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return likes;
    }

    public void addLike(Like like) {
        String sql = "INSERT INTO likes (id, user_id, rating_id) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, like.getId());
            stmt.setObject(2, like.getUserId());
            stmt.setObject(3, like.getRatingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLike(UUID likeId) {
        String sql = "DELETE FROM likes WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, likeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* -------------------- Token Operations -------------------- */

    public List<String> getTokenList() {
        List<String> tokens = new ArrayList<>();
        String sql = "SELECT token FROM tokens";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tokens.add(rs.getString("token"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tokens;
    }

    public void addToken(String token, UUID userId) {
        String sql = "INSERT INTO tokens (token, user_id) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getToken(UUID userId) {
        JWTUtil util = new JWTUtil();
        String sql = "SELECT token FROM tokens WHERE user_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("token");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void removeToken(String token) {
        String sql = "DELETE FROM tokens WHERE token = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
