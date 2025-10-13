package org.example.pkgService;

import org.example.pkgDB.Database;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;
import org.json.JSONObject;

import java.util.*;

public class Service {

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

    public List<Rating> getUserRatings(String username, String password) {
        return db.getRatingsOfUser(username);
    }

    public List<String> getMediaNames() {
        return db.getMediaNameList();
    }

    public void AddMedia(String Username, String MediaName, String Type) {
        List<String> newList = new ArrayList<>();
        Media media = new Media(MediaName, "", MediaType.valueOf(Type), 2025, newList, 0);
        db.addMedia(media);
    }

    public void addRating(UUID userId, UUID mediaId, int Rating, String Comment) {
        Rating rating = new Rating(Rating, Comment, "", userId, mediaId);
        db.addRating(rating);
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

    public Map<String, Object> getQueryRatingData(Map<String, String> queryParams, String method) {
        Map<String, Object> result = new HashMap<>();
        List<Rating> ratingList = db.getRatingList();

        if (method.equals("GET")) {
            if (ratingList != null && !queryParams.isEmpty() && queryParams.containsKey("id")) {
                UUID id = UUID.fromString(queryParams.get("id"));
                for (Rating rating : ratingList) {
                    if (rating.getId().equals(id)) {
                        result.put("response", rating.toJson());
                        result.put("statusCode", 200);
                    }
                }
            } else if (ratingList != null && !queryParams.isEmpty() && queryParams.containsKey("userId")) {
                UUID userId = UUID.fromString(queryParams.get("userId"));
                List<String> responses = new ArrayList<>();
                for (Rating rating : ratingList) {
                    if (rating.getUserId().equals(userId)) {
                        responses.add(rating.toJson());
                    }
                }
                if (!responses.isEmpty()) {
                    result.put("response", responses);
                    result.put("statusCode", 200);
                } else {
                    result.put("response", "ERROR: Not found");
                    result.put("statusCode", 404);
                }
            } else if (ratingList != null && !queryParams.isEmpty() && queryParams.containsKey("mediaId")) {
                UUID mediaId = UUID.fromString(queryParams.get("mediaId"));
                List<String> responses = new ArrayList<>();
                for (Rating rating : ratingList) {
                    if (rating.getMediaId().equals(mediaId)) {
                        responses.add(rating.toJson());
                    }
                }
                if (!responses.isEmpty()) {
                    result.put("response", responses);
                    result.put("statusCode", 200);
                } else {
                    result.put("response", "ERROR: Not found");
                    result.put("statusCode", 404);
                }
            } else {
                result.put("response", db.getRatingData());
                result.put("statusCode", 200);
            }
        } else {
            result.put("statusCode", 405);
            result.put("response", "ERROR: Method Not Allowed");
        }

        return result;
    }

    public Map<String, Object> userLogin(String body, String method) {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = db.getUserList();

        if (method.equals("POST")) {
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
        } else {
            result.put("statusCode", 405);
            result.put("response", "ERROR: Method Not Allowed");
        }

        return result;
    }

    public Map<String, Object> userRegister(String body, String method) {
        Map<String, Object> result = new HashMap<>();

        if (method.equals("POST")) {
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
        } else {
            result.put("statusCode", 405);
            result.put("response", "ERROR: Method Not Allowed");
        }

        return result;
    }
}
