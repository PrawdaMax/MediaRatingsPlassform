package org.example.pkgService;

import org.example.pkgDB.Database;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public String getQueryMediaData(Map<String, String> queryParams) {
        List<Media> mediaList = db.getMediaList();
        String returnval = "Media not found";

        if (mediaList != null && !queryParams.isEmpty() && queryParams.containsKey("id")) {
            UUID id = UUID.fromString(queryParams.get("id"));
            for (Media media : mediaList) {
                if (media.getId().equals(id)) {
                    returnval = media.toJson();
                }
            }
        } else {
            returnval = db.getMediaData();
        }

        return returnval;
    }

    public String getQueryUserData(Map<String, String> queryParams) {
        List<User> userList = db.getUserList();
        String returnval = "User not found";

        if (userList != null && !queryParams.isEmpty() && queryParams.containsKey("id")) {
            UUID id = UUID.fromString(queryParams.get("id"));
            for (User user : userList) {
                if (user.getId().equals(id)) {
                    returnval = user.toJson();
                }
            }
        } else {
            returnval = db.getUserData();
        }

        return returnval;
    }

    public String getQueryRatingData(Map<String, String> queryParams) {
        List<Rating> ratingList = db.getRatingList();
        String returnval = "";

        if (ratingList != null && !queryParams.isEmpty() && queryParams.containsKey("id")) {
            UUID id = UUID.fromString(queryParams.get("id"));
            for (Rating rating : ratingList) {
                if (rating.getId().equals(id)) {
                    returnval = rating.toJson();
                }
            }
        } else if (ratingList != null && !queryParams.isEmpty() && queryParams.containsKey("userId")) {
            UUID userId = UUID.fromString(queryParams.get("userId"));
            for (Rating rating : ratingList) {
                if (rating.getUserId().equals(userId)) {
                    returnval = returnval.concat(rating.toJson());
                }
            }
        } else if (ratingList != null && !queryParams.isEmpty() && queryParams.containsKey("mediaId")) {
            UUID mediaId = UUID.fromString(queryParams.get("mediaId"));
            for (Rating rating : ratingList) {
                if (rating.getMediaId().equals(mediaId)) {
                    returnval = returnval.concat(rating.toJson());
                }
            }
        } else {
            returnval = db.getRatingData();
        }

        return returnval;
    }
}
