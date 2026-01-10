package org.example.pkgServer.pkgRepositories;

import org.example.pkgDB.Database;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import java.sql.*;
import java.util.*;

public class MediaRepository {

    public List<Media> getAll() {
        List<Media> list = new ArrayList<>();
        String sql = "SELECT * FROM media";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Media m = new Media(
                        rs.getString("title"),
                        rs.getString("description"),
                        MediaType.valueOf(rs.getString("media_type")),
                        rs.getInt("year"),
                        new ArrayList<>(),
                        rs.getInt("age_restriction")
                );
                m.setId(UUID.fromString(rs.getString("id")));
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void save(Media media) {
        String sql = "INSERT INTO media (id, title, description, media_type, year, age_restriction) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, media.getId());
            stmt.setString(2, media.getTitle());
            stmt.setString(3, media.getDescription());
            // Casting to OTHER allows Postgres to map the string to your custom ENUM type
            stmt.setObject(4, media.getMediaType().name(), java.sql.Types.OTHER);
            stmt.setInt(5, media.getYear());
            stmt.setInt(6, media.getAgeRestriction());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Media> getMediaList() {
        List<Media> mediaList = new ArrayList<>();
        String sql = "SELECT * FROM media";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Media m = new Media(rs.getString("title"), rs.getString("description"),
                        MediaType.valueOf(rs.getString("media_type")), rs.getInt("year"),
                        new ArrayList<>(), rs.getInt("age_restriction"));
                m.setId(UUID.fromString(rs.getString("id")));
                mediaList.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return mediaList;
    }

    public void deleteMedia(UUID uuid) {
        String sql = "DELETE FROM media WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<String> getMediaNameList() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT title FROM media";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { list.add(rs.getString("title")); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Media getById(UUID id) {
        String sql = "SELECT * FROM media WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Media m = new Media(
                            rs.getString("title"),
                            rs.getString("description"),
                            MediaType.valueOf(rs.getString("media_type")),
                            rs.getInt("year"),
                            new ArrayList<>(), // Handling genres would require a separate join/query
                            rs.getInt("age_restriction")
                    );
                    m.setId(UUID.fromString(rs.getString("id")));

                    return m;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Media updateMedia(Map<String, Object> updates, Media media) {
        if (updates == null || updates.isEmpty() || media == null) {
            return media;
        }

        StringBuilder sql = new StringBuilder("UPDATE media SET ");
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
                String columnName = (String) updates.keySet().toArray()[i];
                Object value = values.get(i);

                if (columnName.equals("media_type")) {
                    stmt.setObject(i + 1, value.toString(), java.sql.Types.OTHER);
                } else {
                    stmt.setObject(i + 1, value);
                }
            }

            stmt.setObject(values.size() + 1, media.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                applyChangesToObject(media, updates);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return media;
    }

    private void applyChangesToObject(Media media, Map<String, Object> updates) {
        if (updates.containsKey("title")) media.setTitle((String) updates.get("title"));
        if (updates.containsKey("description")) media.setDescription((String) updates.get("description"));
        if (updates.containsKey("year")) media.setYear((int) updates.get("year"));
        if (updates.containsKey("age_restriction")) media.setAgeRestriction((int) updates.get("age_restriction"));
        if (updates.containsKey("media_type")) {
            media.setMediaType(MediaType.valueOf(updates.get("media_type").toString()));
        }
    }
}