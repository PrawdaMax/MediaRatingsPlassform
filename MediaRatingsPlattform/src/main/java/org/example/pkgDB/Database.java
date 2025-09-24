package org.example.pkgDB;

import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Media> mediaList = new ArrayList<>();
    private List<Rating> ratingList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    public boolean addMedia(Media media) {
        mediaList.add(media);
        return true;
    }

    public boolean deleteMedia(Media media) {
        mediaList.remove(media);
        return true;
    }

    public boolean addRating(Rating rating) {
        ratingList.add(rating);
        return true;
    }

    public boolean deleteRating(Rating rating) {
        ratingList.remove(rating);
        return true;
    }

    public boolean addUser(User user) {
        userList.add(user);
        return true;
    }

    public boolean deleteUser(User user) {
        userList.remove(user);
        return true;
    }

    public boolean findUser(String username, String password) {
        for (User user : userList) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getMediaNameList() {
        List<String> list = new ArrayList<>();
        for (Media media : mediaList) {
            list.add(media.getTitle());
        }
        return list;
    }

    public List<Rating> getRatingsOfUser(String username) {
        List<Rating> ratings = new ArrayList<>();
        for (Rating rating : ratingList) {
            if (rating.getUsername().equals(username)) {
                ratings.add(rating);
            }
        }
        return ratings;
    }
}
