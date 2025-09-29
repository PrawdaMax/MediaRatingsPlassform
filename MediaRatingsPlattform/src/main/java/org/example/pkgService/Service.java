package org.example.pkgService;

import org.example.pkgDB.Database;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;

import java.util.ArrayList;
import java.util.List;

public class Service {

    Database db = new Database();

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

    public void addRating(String Username, String MediaName, int Rating, String Comment) {
        Rating rating = new Rating(Rating, Comment, "", Username, MediaName);
        db.addRating(rating);
    }

    public String getAllData() {
        return db.getAllData();
    }

    public String getMediaData() {
        return db.getMediaData();
    }

    public String getUserData() {
        return db.getUserData();
    }

    public String getRatingData() {
        return  db.getRatingData();
    }
}
