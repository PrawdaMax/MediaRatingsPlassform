package org.example.pkgService;

import org.example.pkgObj.Rating;
import org.example.pkgObj.User;
import org.example.pkgObj.UserWithRatings;
import org.example.pkgServer.pkgRepositories.RatingRepository;
import org.example.pkgServer.pkgRepositories.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardService {
    private final UserRepository userRepo;
    private final RatingRepository ratingRepo;

    public LeaderboardService(UserRepository userRepo, RatingRepository ratingRepo) {
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
    }

    /**
     * Generates a leaderboard of users ranked by the number of ratings they have submitted.
     */
    public Map<String, Object> getLeaderboard() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<User> userList = userRepo.getAll();
            List<Rating> allRatings = ratingRepo.getRatingList(); // Assuming RatingRepo has an getAll() method

            // Group ratings by UserID for efficient lookup
            Map<UUID, List<Rating>> ratingsByUserId = allRatings.stream()
                    .collect(Collectors.groupingBy(Rating::getUserId));

            // Map users to the DTO and sort by the size of their rating list (descending)
            List<UserWithRatings> leaderboard = userList.stream()
                    .map(user -> new UserWithRatings(
                            user,
                            ratingsByUserId.getOrDefault(user.getId(), new ArrayList<>())
                    ))
                    .sorted(Comparator.comparingInt(UserWithRatings::getRatingsCount).reversed())
                    .collect(Collectors.toList());

            // Format as JSON Array
            String jsonResponse = leaderboard.stream()
                    .map(UserWithRatings::toJson)
                    .collect(Collectors.joining(",\n", "[\n", "\n]"));

            result.put("response", jsonResponse);
            result.put("statusCode", 200);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("response", "{\"error\": \"Failed to generate leaderboard\"}");
            result.put("statusCode", 500);
        }

        return result;
    }
}