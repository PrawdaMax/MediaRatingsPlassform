package org.example.pkgService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;
import org.example.pkgMisc.MediaType;
import org.example.pkgServer.pkgRepositories.MediaRepository;
import org.example.pkgServer.pkgRepositories.RatingRepository;
import org.example.pkgServer.pkgRepositories.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class MediaService {
    private final MediaRepository mediaRepo;
    private final UserRepository userRepo;
    private final RatingRepository ratingRepo;
    private final Gson gson = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    public MediaService(MediaRepository mediaRepo, UserRepository userRepo, RatingRepository ratingRepo) {
        this.mediaRepo = mediaRepo;
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
    }

    // --- GET /media ---
    public Map<String, Object> getAllMedia(Map<String, String> query) {
        List<Media> filteredList = mediaRepo.getAll().stream()
                .filter(m -> !query.containsKey("title") || m.getTitle().toLowerCase().contains(query.get("title").toLowerCase()))
                .filter(m -> !query.containsKey("mediaType") || m.getMediaType().name().equalsIgnoreCase(query.get("mediaType")))
                .filter(m -> !query.containsKey("releaseYear") || m.getYear() == Integer.parseInt(query.get("releaseYear")))
                .collect(Collectors.toList());

        // Handle Sorting
        if (query.containsKey("sortBy")) {
            String sortBy = query.get("sortBy");
            if ("title".equals(sortBy)) filteredList.sort(Comparator.comparing(Media::getTitle));
            if ("releaseYear".equals(sortBy)) filteredList.sort(Comparator.comparingInt(Media::getYear));
        }

        return success(200, filteredList.stream().map(Media::toJson).collect(Collectors.joining(",", "[", "]")));
    }

    // --- POST /media ---
    public Map<String, Object> postMedia(String body) {
        try {
            Media newMedia = gson.fromJson(body, Media.class);

            if (newMedia.getId() == null) {
                newMedia.setId(UUID.randomUUID());
            }

            // Check for duplicates
            boolean exists = mediaRepo.getAll().stream()
                    .anyMatch(m -> m.getTitle().equalsIgnoreCase(newMedia.getTitle()) && m.getMediaType() == newMedia.getMediaType());

            if (exists) return error(409, "Media already exists");

            mediaRepo.save(newMedia);
            return success(201, newMedia.toJson());
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

    // --- GET /media/{id} ---
    public Map<String, Object> getMedia(UUID id) {
        Media media = mediaRepo.getAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst().orElse(null);

        return (media != null) ? success(200, media.toJson()) : error(404, "Not found");
    }

    // --- PUT /media/{id} ---
    public Map<String, Object> updateMedia(UUID id, String body) {
        try {
            Media existing = mediaRepo.getAll().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
            if (existing == null) return error(404, "Not found");

            Map<String, Object> updates = gson.fromJson(body, Map.class);

            updates.replaceAll((key, value) -> {
                if (value instanceof Long) {
                    Long l = (Long) value;
                    if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                        return l.intValue();
                    }
                }
                return value; // Keep original if not a Long or too big
            });

            Media media = mediaRepo.getById(id);

            media = mediaRepo.updateMedia(updates, media);

            return success(200, media.toJson());
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

    // --- DELETE /media/{id} ---
    public Map<String, Object> deleteMedia(UUID id) {
        Media existing = mediaRepo.getAll().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        if (existing == null) return error(404, "Not found");

        mediaRepo.deleteMedia(id);

        return success(204, gson.toJson(existing, Media.class));
    }

    // --- POST /media/{id}/ratings ---
    public Map<String, Object> addRating(UUID mediaId, String body) {
        try {
            Map data = gson.fromJson(body, Map.class);
            Rating rating = new Rating(
                    ((Number) data.get("stars")).intValue(),
                    (String) data.get("comment"),
                    new java.sql.Timestamp(System.currentTimeMillis()).toString(),
                    UUID.fromString((String) data.get("user")),
                    mediaId
            );
            ratingRepo.addRating(rating);
            return success(201, rating.toJson());
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

    // --- POST /media/{id}/favorite ---
    public Map<String, Object> markAsFavorite(UUID mediaId, String body) {
        try {
            Map data = gson.fromJson(body, Map.class);
            UUID userId = UUID.fromString((String) data.get("user"));
            User user = userRepo.getById(userId);

            if (user == null) return error(404, "User not found");
            if (user.getFavorites().contains(mediaId)) return error(400, "Already favorite");

            user.addFavorite(mediaId);
            // userRepo.update(user); // Persistence logic
            return success(200, user.toJson());
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

    // --- DELETE /media/{id}/favorite ---
    public Map<String, Object> unmarkAsFavorite(UUID mediaId, String body) {
        try {
            Map data = gson.fromJson(body, Map.class);
            UUID userId = UUID.fromString((String) data.get("user"));
            User user = userRepo.getById(userId);

            if (user != null) {
                user.deleteFavorite(mediaId);
                return success(200, user.toJson());
            }
            return error(400, "Not a favorite");
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

    // --- Helpers ---
    private Map<String, Object> success(int code, String response) {
        Map<String, Object> res = new HashMap<>();
        res.put("statusCode", code);
        res.put("response", response);
        return res;
    }

    private Map<String, Object> error(int code, String msg) {
        Map<String, Object> res = new HashMap<>();
        res.put("statusCode", code);
        res.put("response", "ERROR: " + msg);
        return res;
    }
}