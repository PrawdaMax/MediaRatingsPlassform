package org.example.pkgService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.pkgDB.Database;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.*;
import org.example.pkgServer.pkgToken.JWTUtil;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Service {
    Gson gson = new Gson();
    Database db;

    public Service(Database database) {
        this.db = database;
    }

    public void addUser(String username, String password) {
        db.addUser(new User(username, password));
    }

    public void deleteUser(String username, String password) {
        db.deleteUser(new User(username, password));
    }

    public boolean checkUser (String username, String password) {
        return db.findUser(username, password);
    }

    public List<String> getMediaNames() {
        return db.getMediaNameList();
    }

    public void AddMedia(String Username, String MediaName, String Type) {
        List<String> newList = new ArrayList<>();
        Media media = new Media(MediaName, "", MediaType.valueOf(Type), 2025, newList, 0);
        db.addMedia(media);
    }

    public String getAllData() {
        return db.getAllData();
    }

    public Map<String, Object> apiData(String method) {
        Map<String, Object> result = new HashMap<>();

        if (method.equals("GET")) {
            try {
                result.put("statusCode", 200);
                result.put("response", getAllData());
            } catch (Exception e) {
                result.put("statusCode", 500);
                result.put("response", e.getMessage());
            }
        } else {
            result.put("statusCode", 405);
            result.put("response", "ERROR: Method Not Allowed");
        }
        return result;
    }

    public Map<String, Object> getQueryMediaData(Map<String, String> queryParams, String method) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();

        if (method.equals("GET")) {
            if (mediaList != null && !queryParams.isEmpty() && queryParams.containsKey("id")) {
                UUID id = UUID.fromString(queryParams.get("id"));
                for (Media media : mediaList) {
                    if (media.getId().equals(id)) {
                        result.put("response",media.toJson());
                        result.put("statusCode", 200);
                    }
                }
            } else {
                result.put("response",db.getMediaData());
                result.put("statusCode", 200);
            }
        } else {
            result.put("statusCode", 405);
            result.put("response", "ERROR: Method Not Allowed");
        }
        
        return result;
    }

    public Map<String, Object>  getQueryUserData(Map<String, String> queryParams, String method) {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();

        if (method.equals("GET")) {
            if (userList != null && !queryParams.isEmpty() && queryParams.containsKey("id")) {
                UUID id = UUID.fromString(queryParams.get("id"));
                for (User user : userList) {
                    if (user.getId().equals(id)) {
                        result.put("response",user.toJson());
                        result.put("statusCode", 200);
                    }
                }
            } else {
                result.put("response", db.getUserData());
                result.put("statusCode", 200);
            }
        } else {
            result.put("statusCode", 405);
            result.put("response", "ERROR: Method Not Allowed");
        }

        return result;
    }

    public Map<String, Object> userLogin(String body) {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();
        JWTUtil util = new JWTUtil();

        if (!body.equals("")) {
            JSONObject json = new JSONObject(body);
            String username = json.getString("username");
            String password = json.getString("password");
            User user = new User(username, password);
            boolean res = false;
            for (User u : userList) {
                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                    res = true;
                    user = u;
                }
            }
            if (res) {
                if(!checkForToken(user.getId())) {
                    String token = util.generateToken(user.getId().toString(), user.getUsername());
                    db.addToken(token);

                    JsonObject obj = JsonParser.parseString(user.toJson()).getAsJsonObject();
                    obj.addProperty("token", token);

                    result.put("response", gson.toJson(obj) + "\n");
                    result.put("statusCode", 200);
                } else {
                    String token = db.getToken(user.getId());

                    JsonObject obj = JsonParser.parseString(user.toJson()).getAsJsonObject();
                    obj.addProperty("token", token);

                    result.put("response", gson.toJson(obj) + gson.toJson(token, String.class));
                    result.put("statusCode", 200);
                }

            } else  {
                result.put("response", "ERROR: Not found");
                result.put("statusCode", 404);
            }
        }

        return result;
    }

    public Map<String, Object> userRegister(String body) {
        Map<String, Object> result = new HashMap<>();

        if (!body.equals("")) {
            JSONObject json = new JSONObject(body);
            String username = json.getString("username");
            String password = json.getString("password");
            User user = new User(username, password);
            if (db.addUser(user))  {
                result.put("response", user.toJson());
                result.put("statusCode", 201);
            } else {
                result.put("response", "ERROR: Already in use");
                result.put("statusCode", 409);
            }
        }

        return result;
    }

    public Map<String, Object> getUser(UUID userId) {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();
        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        for (User user : userList) {
            if (user.getId().equals(userId)) {
                result.put("response", user.toJson());
                result.put("statusCode", 200);
            }
        }

        return result;
    }

    public Map<String, Object> createUser(UUID userId, String body) {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();
        result.put("response", "ERROR: ");

        return result;
    }

    public Map<String, Object> getUserRatings(UUID uuid) {
        Map<String, Object> result = new HashMap<>();
        List<Rating> ratingList = db.getRatingsOfUser(uuid);
        StringBuilder sb = new StringBuilder();

        sb.append("[\n");
        if (ratingList != null && !ratingList.isEmpty()) {
            for (Rating rating : ratingList) {
                sb.append(rating.toJson());
                sb.append(",\n");
            }
            sb.append("]");
            result.put("response", sb.toString());
            result.put("statusCode", 200);
        } else {
            result.put("response", "ERROR: Not found");
            result.put("statusCode", 404);
        }

        return result;
    }

    public Map<String, Object> updateUser(UUID uuid, String body) {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();
        Map<String, Object> map = new Gson().fromJson(body, Map.class);

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            for (User user : userList) {
                if (user.getId().equals(uuid)) {
                    user.setUsername((String) map.get("username"));
                    user.setPassword((String) map.get("password"));

                    result.put("response", user.toJson());
                    result.put("statusCode", 200);
                }
            }
        } catch (Exception ex) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> getAllMedia(Map<String, String> queryParams) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();
        List<Media> filteredList = new ArrayList<>();

        try {
            for (Media media : mediaList) {
                boolean matches = true;

                if (queryParams.containsKey("title")) {
                    String queryTitle = queryParams.get("title").toLowerCase();
                    if (!media.getTitle().toLowerCase().contains(queryTitle)) {
                        matches = false;
                    }
                }

                if (queryParams.containsKey("genre")) {
                    String queryGenre = queryParams.get("genre").toLowerCase();

                    boolean genreMatch = false;

                    List<String> genreList = media.getGenres();

                    for (String genre : genreList) {
                        if (genre.toLowerCase().equals(queryGenre)) {
                            genreMatch = true;
                            break;
                        }
                    }

                    if (!genreMatch) {
                        matches = false;
                    }
                }

                if (queryParams.containsKey("mediaType")) {
                    if (!media.getMediaType().equals(MediaType.valueOf(queryParams.get("mediaType").toLowerCase()))) {
                        matches = false;
                    }
                }

                if (queryParams.containsKey("releaseYear")) {
                    int year = Integer.parseInt(queryParams.get("releaseYear"));
                    if (media.getYear() != year) {
                        matches = false;
                    }
                }

                if (queryParams.containsKey("ageRestriction")) {
                    int age = Integer.parseInt(queryParams.get("ageRestriction"));
                    if (media.getAgeRestriction() != age) {
                        matches = false;
                    }
                }

                if (matches) {
                    filteredList.add(media);
                }
            }

            if (queryParams.containsKey("sortBy")) {
                String sortBy = queryParams.get("sortBy");

                switch (sortBy) {
                    case "title":
                        filteredList.sort(Comparator.comparing(Media::getTitle));
                        break;
                    case "releaseYear":
                        filteredList.sort(Comparator.comparingInt(Media::getYear));
                        break;
                    case "ageRestriction":
                        filteredList.sort(Comparator.comparingInt(Media::getAgeRestriction));
                        break;
                }
            }

            StringBuilder sb = new StringBuilder();

            sb.append("[\n");
            for (Media media : filteredList) {
                sb.append(media.toJson());
                sb.append(",\n");
            }
            sb.append("]");

            result.put("response", sb.toString());
            result.put("statusCode", 200);
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> postMedia (String body) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();

        try {
            Map<String, Object> data = gson.fromJson(body, Map.class);

            String title = (String) data.get("title");
            String description = (String) data.get("description");
            String mediaType = (String) data.get("mediaType");
            double releaseYear = (Double) data.get("releaseYear"); // Gson uses Double for numbers
            List<String> genres = (List<String>) data.get("genres");
            double ageRestriction = (Double) data.get("ageRestriction");

            Media newMedia = new Media(title, description, MediaType.valueOf(mediaType), (int) releaseYear, genres, (int) ageRestriction);
            boolean isInside = false;
            for (Media media : mediaList) {
                if (media.getTitle().equals(newMedia.getTitle()) && media.getMediaType().equals(newMedia.getMediaType())) {
                    isInside = true;
                    break;
                }
            }

            if (!isInside) {
                db.addMedia(newMedia);
                result.put("response", newMedia.toJson());
                result.put("statusCode", 201);
            } else {
                result.put("response", "ERROR: Already exists");
                result.put("statusCode", 409);
            }
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> getMedia (UUID uuid) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();
        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            for (Media media : mediaList) {
                if (media.getId().equals(uuid)) {
                    result.put("response", media.toJson());
                    result.put("statusCode", 200);
                }
                break;
            }
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> updateMedia(UUID uuid, String body) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();
        Map<String, Object> map = new Gson().fromJson(body, Map.class);

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            for (Media media : mediaList) {
                if (media.getId().equals(uuid)) {
                    Map<String, Object> newMedia = new Gson().fromJson(body, Map.class);

                    media.setTitle((String) newMedia.get("title"));
                    media.setDescription((String) newMedia.get("description"));
                    media.setMediaType(MediaType.valueOf((String)newMedia.get("mediaType")));
                    media.setYear(((Double) newMedia.get("releaseYear")).intValue());
                    media.setAgeRestriction(((Double) newMedia.get("ageRestriction")).intValue());
                    media.setGenres((List<String>) newMedia.get("genres"));

                    result.put("response", media.toJson());
                    result.put("statusCode", 200);
                    break;
                }
            }
        } catch (Exception ex) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> deleteMedia(UUID uuid) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();
        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            Iterator<Media> iterator = mediaList.iterator();
            while (iterator.hasNext()) {
                Media media = iterator.next();
                if (media.getId().equals(uuid)) {
                    iterator.remove();
                    db.deleteMedia(media.getId());
                    result.put("response", media.toJson());
                    result.put("statusCode", 204);
                    break;
                }
            }
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
        }

        return result;
    }

    public Map<String, Object> addRating(UUID uuid, String body) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> newRating = new Gson().fromJson(body, Map.class);

            int value = (int) Math.round((double) (newRating.get("stars")));
            String comment = (String) newRating.get("comment");
            UUID userid = UUID.fromString((String) newRating.get("user"));

            Rating ratingToAdd = new Rating(value, comment, "2025", userid, uuid);

            db.addRating(ratingToAdd);
            result.put("response", ratingToAdd.toJson());
            result.put("statusCode", 201);
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return  result;
    }

    public Map<String, Object> updateRating(UUID uuid, String body) {
        Map<String, Object> result = new HashMap<>();
        List<Rating> ratingList = db.getRatingList();

        try {
            Map<String, Object> newRating = new Gson().fromJson(body, Map.class);

            int value = (int) Math.round((double) (newRating.get("stars")));
            String comment = (String) newRating.get("comment");

            for (Rating rating : ratingList) {
                if (rating.getId().equals(uuid)) {
                    rating.setValue(value);
                    rating.setComment(comment);

                    result.put("response", rating.toJson());
                    result.put("statusCode", 200);
                }
            }
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return  result;
    }

    public Map<String, Object> deleteRating(UUID uuid) {
        Map<String, Object> result = new HashMap<>();
        List<Rating> ratingList = db.getRatingList();


        return result;
    }

    public Map<String, Object> confirmRating(UUID uuid) {
        Map<String, Object> result = new HashMap<>();
        List<Rating> ratingList = db.getRatingList();

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            for (Rating rating : ratingList) {
                if (rating.getId().equals(uuid)) {
                    rating.confirm();
                    result.put("response", rating.toJson());
                    result.put("statusCode", 200);
                }
            }
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();

        StringBuilder sb = new StringBuilder();

        for (User user : userList) {
            sb.append(user.toJson());
        }

        result.put("response", sb.toString());
        result.put("statusCode", 200);

        return result;
    }

    public Map<String, Object> getUserFavorites(UUID uuid) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            StringBuilder sb = new StringBuilder();
            User user = db.getUser(uuid);
            List<UUID> favorites = user.getFavorites();
            List<Media> favoriteMedia = mediaList.stream()
                    .filter(media -> favorites.contains(media.getId()))
                    .toList();

            for (Media media : favoriteMedia) {
                sb.append(media.toJson());
            }

            result.put("response", sb.toString());
            result.put("statusCode", 200);

        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 409);
        }

        return result;
    }

    public Map<String, Object> markAsFavorite(UUID uuid, String body) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userMap = new Gson().fromJson(body, Map.class);

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            UUID userid = UUID.fromString((String) userMap.get("user"));
            User user  = db.getUser(userid);

            boolean isInside = false;
            for (UUID favorite : user.getFavorites()) {
                if (favorite.equals(uuid)) {
                    isInside = true;
                    break;
                }
            }
            if (!isInside) {
                user.addFavorite(uuid);

                result.put("response", user.toJson());
                result.put("statusCode", 200);
            } else {
                result.put("response", "ERROR: Already Favorite");
                result.put("statusCode", 400);
            }

        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> unmarkAsFavorite(UUID uuid, String body) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userMap = new Gson().fromJson(body, Map.class);

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            UUID userid = UUID.fromString((String) userMap.get("user"));
            User user  = db.getUser(userid);

            boolean isInside = false;
            for (UUID favorite : user.getFavorites()) {
                if (favorite.equals(uuid)) {
                    isInside = true;
                    break;
                }
            }
            if (isInside) {
                user.deleteFavorite(uuid);

                result.put("response", user.toJson());
                result.put("statusCode", 200);
            } else {
                result.put("response", "ERROR: No Favorite");
                result.put("statusCode", 400);
            }

        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> getLeaderboard() {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();
        List<Rating> ratingList = db.getRatingList();

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {

            Map<UUID, List<Rating>> ratingsByUserId = ratingList.stream()
                    .collect(Collectors.groupingBy(Rating::getUserId));

            List<UserWithRatings> userWithRatingsList = userList.stream()
                    .map(user -> new UserWithRatings(
                            user,
                            ratingsByUserId.getOrDefault(user.getId(), new ArrayList<>())
                    ))
                    .sorted(Comparator.comparingInt((UserWithRatings uwr) -> uwr.getRatings().size()).reversed())
                    .collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();

            sb.append("[\n");
            for (UserWithRatings userWithRatings : userWithRatingsList) {
                sb.append(userWithRatings.toJson());
                sb.append(",\n");
            }
            sb.append("]");
            result.put("response", sb.toString());
            result.put("statusCode", 200);

        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> getUserRecs(UUID uuid, Map<String, String> queryParams) {
        Map<String, Object> result = new HashMap<>();
        List<Media> mediaList = db.getMediaList();

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {

            if (queryParams.containsKey("type")) {
                if (queryParams.get("type").equals("genre")) {
                    User user = db.getUser(uuid);
                    List<String> favGenres = user.getFavoriteGenres();

                    List<Media> matchingMedia = mediaList.stream()
                            .filter(media -> !Collections.disjoint(media.getGenres(), favGenres))
                            .sorted(Comparator.comparingInt((Media media) ->
                                    (int) media.getGenres().stream()
                                            .filter(favGenres::contains)
                                            .count()
                            ).reversed())
                            .collect(Collectors.toList());

                    StringBuilder sb = new StringBuilder();
                    sb.append("[\n");
                    for (Media media : matchingMedia) {
                        sb.append(media.toJson());
                        sb.append(",\n");
                    }
                    sb.append("]");

                    result.put("response", sb.toString());
                    result.put("statusCode", 200);

                } else if (queryParams.get("type").equals("content")) {

                    User user = db.getUser(uuid);
                    List<UUID> favMedia = user.getFavorites();
                    List<Media> favoriteMedia = new ArrayList<>();
                    List<String> genres = new ArrayList<>();

                    for (Media media : mediaList) {
                        if (favMedia.contains(media.getId())) {
                            favoriteMedia.add(media);
                        }
                    }

                    for (Media media : favoriteMedia) {
                        genres.addAll(media.getGenres());
                    }

                    Set<String> set = new LinkedHashSet<>(genres);
                    ArrayList<String> uniqueFavorites = new ArrayList<>(set);

                    List<Media> matchingMedia = mediaList.stream()
                            .filter(media -> !Collections.disjoint(media.getGenres(), uniqueFavorites))
                            .sorted(Comparator.comparingInt((Media media) ->
                                    (int) media.getGenres().stream()
                                            .filter(uniqueFavorites::contains)
                                            .count()
                            ).reversed())
                            .collect(Collectors.toList());

                    StringBuilder sb = new StringBuilder();
                    sb.append("[\n");
                    for (Media media : matchingMedia) {
                        sb.append(media.toJson());
                        sb.append(",\n");
                    }
                    sb.append("]");
                    result.put("response", sb.toString());
                    result.put("statusCode", 200);
                }
            }

        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public Map<String, Object> likeRating(UUID uuid, String body) {
        Map<String, Object> result = new HashMap<>();

        result.put("response", "ERROR: Not found");
        result.put("statusCode", 404);

        try {
            Map request = gson.fromJson(body, Map.class);
            UUID userId = UUID.fromString(request.get("user").toString());

            Like like = new Like(userId, uuid);
            List<Like> likeList = db.getLikes();
            boolean hasLiked = false;

            for (Like listlike : likeList) {
                if (listlike.getUserId().equals(like.getUserId()) && listlike.getRatingId().equals(like.getRatingId())) {
                    hasLiked = true;
                    break;
                }
            }

            if (!hasLiked) {
                db.addLike(like);
                result.put("response", "Rating Liked");
                result.put("statusCode", 200);
            } else {
                result.put("response", "ERROR: Rating already liked");
                result.put("statusCode", 400);
            }


        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
        }

        return result;
    }

    public boolean checkForToken(UUID uuid) {
        boolean ret = false;
        JWTUtil jwtUtil = new JWTUtil();
        List<String> tokenList = db.getTokenList();

        Iterator<String> iterator = tokenList.iterator();
        for (Iterator<String> it = iterator; it.hasNext(); ) {
            String token = it.next();

            if (jwtUtil.getUserIdFromToken(token).equals(uuid.toString())) {
                if (jwtUtil.isTokenExpired(token)) {
                    ret = false;
                    db.removeToken(token);
                } else {
                    ret = true;
                }
            } else {
                ret = false;
            }
        }

        return ret;
    }
}
