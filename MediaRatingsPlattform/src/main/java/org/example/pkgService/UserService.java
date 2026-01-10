package org.example.pkgService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.pkgObj.*;
import org.example.pkgServer.pkgRepositories.MediaRepository;
import org.example.pkgServer.pkgRepositories.RatingRepository;
import org.example.pkgServer.pkgRepositories.TokenRepository;
import org.example.pkgServer.pkgRepositories.UserRepository;
import org.example.pkgServer.pkgToken.JWTUtil;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final RatingRepository ratingRepo;
    private final MediaRepository mediaRepo;
    private final JWTUtil jwtUtil = new JWTUtil();
    private final Gson gson = new Gson();

    public UserService(UserRepository userRepo, TokenRepository tokenRepo, RatingRepository ratingRepo, MediaRepository mediaRepo) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.ratingRepo = ratingRepo;
        this.mediaRepo = mediaRepo;
    }

    // --- GET /users ---
    public Map<String, Object> getAllUsers() {
        String response = userRepo.getAll().stream()
                .map(User::toJson)
                .collect(Collectors.joining(",", "[", "]"));
        return success(200, response);
    }

    // --- POST /users (Register) ---
    public Map<String, Object> registerUser(String body) {
        JSONObject json = new JSONObject(body);
        User user = new User(json.getString("username"), json.getString("password"));
        if (userRepo.addUser(user)) {
            return success(201, user.toJson());
        }
        return error(409, "Username already in use");
    }

    // --- POST /sessions (Login) ---
    public Map<String, Object> loginUser(String body) {
        JSONObject json = new JSONObject(body);
        String username = json.getString("username");
        String password = json.getString("password");

        User user = userRepo.getAll().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst().orElse(null);

        if (user == null) return error(404, "Invalid credentials");

        String token = tokenRepo.getTokenByUserId(user.getId());
        if (token.isEmpty() || jwtUtil.isTokenExpired(token)) {
            token = jwtUtil.generateToken(user.getId().toString(), user.getUsername());
            tokenRepo.saveToken(token, user.getId());
        }

        JsonObject obj = JsonParser.parseString(user.toJson()).getAsJsonObject();
        obj.addProperty("token", token);
        return success(200, gson.toJson(obj));
    }

    // --- GET /users/{id} ---
    public Map<String, Object> getUserProfile(UUID id) {
        User user = userRepo.getById(id);
        return (user != null) ? success(200, user.toJson()) : error(404, "User not found");
    }

    // --- PUT /users/{id} ---
    public Map<String, Object> updateUserProfile(UUID id, String body) {
        User user = userRepo.getById(id);
        if (user == null) return error(404, "User not found");

        Map<String, Object> data = gson.fromJson(body, Map.class);
        user.setUsername((String) data.get("username"));
        user.setPassword((String) data.get("password"));

        // userRepo.update(user);
        return success(200, user.toJson());
    }

    // --- GET /users/{id}/ratings ---
    public Map<String, Object> getUserRatings(UUID id) {
        List<Rating> ratings = ratingRepo.getRatingList().stream()
                .filter(r -> r.getUserId().equals(id))
                .collect(Collectors.toList());

        if (ratings.isEmpty()) return error(404, "No ratings found");
        return success(200, ratings.stream().map(Rating::toJson).collect(Collectors.joining(",", "[", "]")));
    }

    // --- GET /users/{id}/favorites ---
    public Map<String, Object> getUserFavorites(UUID id) {
        User user = userRepo.getById(id);
        if (user == null) return error(404, "User not found");

        List<UUID> favIds = user.getFavorites();
        List<Media> mediaList = mediaRepo.getAll().stream()
                .filter(m -> favIds.contains(m.getId()))
                .collect(Collectors.toList());

        return success(200, mediaList.stream().map(Media::toJson).collect(Collectors.joining(",", "[", "]")));
    }

    // --- GET /users/{id}/recommendations ---
    public Map<String, Object> getUserRecommendations(UUID id, Map<String, String> query) {
        User user = userRepo.getById(id);
        if (user == null) return error(404, "User not found");

        List<Media> allMedia = mediaRepo.getAll();
        List<Media> recs = new ArrayList<>();

        if ("genre".equals(query.get("type"))) {
            List<String> favGenres = user.getFavoriteGenres();
            recs = allMedia.stream()
                    .filter(m -> !Collections.disjoint(m.getGenres(), favGenres))
                    .collect(Collectors.toList());
        }

        return success(200, recs.stream().map(Media::toJson).collect(Collectors.joining(",", "[", "]")));
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