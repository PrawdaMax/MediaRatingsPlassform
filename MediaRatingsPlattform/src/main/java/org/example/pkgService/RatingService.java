package org.example.pkgService;

import com.google.gson.Gson;
import org.example.pkgObj.Rating;
import org.example.pkgObj.Like;
import org.example.pkgServer.pkgRepositories.RatingRepository;

import java.util.*;

public class RatingService {
    private final RatingRepository ratingRepo;
    private final Gson gson = new Gson();

    public RatingService(RatingRepository ratingRepo) {
        this.ratingRepo = ratingRepo;
    }

    /**
     * Updates an existing rating's stars and comment.
     * Path: PUT /ratings/{id}
     */
    public Map<String, Object> updateRating(UUID id, String body) {
        try {
            // Fetch current ratings
            List<Rating> ratingList = ratingRepo.getRatingList();
            Rating ratingToUpdate = ratingList.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (ratingToUpdate == null) {
                return error(404, "Rating not found");
            }

            Map<String, Object> newRatingData = gson.fromJson(body, Map.class);

            // Update fields
            if (newRatingData.containsKey("stars")) {
                int value = (int) Math.round((double) (newRatingData.get("stars")));
                ratingToUpdate.setValue(value);
            }
            if (newRatingData.containsKey("comment")) {
                ratingToUpdate.setComment((String) newRatingData.get("comment"));
            }

            ratingToUpdate = ratingRepo.updateRating(newRatingData, ratingToUpdate);

            return success(200, gson.toJson(ratingToUpdate, Rating.class));
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

    /**
     * Adds a "Like" entry for a specific rating.
     * Path: POST /ratings/{id}/like
     */
    public Map<String, Object> likeRating(UUID ratingId, String body) {
        try {
            Map request = gson.fromJson(body, Map.class);
            UUID userId = UUID.fromString(request.get("user").toString());

            // Check if user already liked this rating
            List<Like> currentLikes = ratingRepo.getLikes();
            boolean alreadyLiked = currentLikes.stream()
                    .anyMatch(l -> l.getUserId().equals(userId) && l.getRatingId().equals(ratingId));

            if (alreadyLiked) {
                return error(400, "Rating already liked");
            }

            Like newLike = new Like(userId, ratingId);
            ratingRepo.addLike(newLike);
            return success(200, gson.toJson(newLike, Like.class));
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

    /**
     * Confirms a rating (e.g., admin verification).
     * Path: POST /ratings/{id}/confirm
     */
    public Map<String, Object> confirmRating(UUID id) {
        try {
            List<Rating> ratingList = ratingRepo.getRatingList();
            Rating rating = ratingList.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (rating == null) {
                return error(404, "Rating not found");
            }

            rating.confirm();
            // ratingRepo.update(rating); // Ensure state is saved to DB

            return success(200, rating.toJson());
        } catch (Exception e) {
            return error(400, "Bad Request");
        }
    }

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