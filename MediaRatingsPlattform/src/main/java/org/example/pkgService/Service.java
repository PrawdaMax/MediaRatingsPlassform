package org.example.pkgService;

import com.google.gson.Gson;
import org.example.pkgDB.Database;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;
import org.json.JSONObject;

import java.util.*;

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
                result.put("response", user.toJson());
                result.put("statusCode", 200);
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
                result.put("statusCode", 200);
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
                result.put("statusCode", 200);
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
                    iterator.remove(); // Safe
                    result.put("response", "Media Deleted");
                    result.put("statusCode", 200);
                    break;
                }
            }
        } catch (Exception e) {
            result.put("response", "ERROR: Bad Request");
            result.put("statusCode", 400);
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
            result.put("response", "Added Rating");
            result.put("statusCode", 200);
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
}
