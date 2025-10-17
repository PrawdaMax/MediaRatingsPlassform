package org.example.pkgDB;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.pkgObj.Like;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;
import org.example.pkgServer.pkgToken.JWTUtil;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//RFC 9526 für Ids

public class Database {
    private List<Media> mediaList;
    private List<Rating> ratingList;
    private List<User> userList;
    private List<Like> likesList;
    private List<String> tokenList;

    public Database() throws FileNotFoundException {
        Gson gson = new GsonBuilder().create();

        InputStream input = Database.class.getResourceAsStream("/TestData.json");

        if (input == null) {
            throw new RuntimeException("Could not find TestData.json in resources.");
        }

        InputStreamReader reader = new InputStreamReader(input);

        MediaLibrary library = gson.fromJson(reader, MediaLibrary.class);

        this.userList = library.Users;
        this.mediaList = library.Media;
        this.ratingList = library.Ratings;
        this.likesList = new ArrayList<>();
        this.tokenList = new ArrayList<>();
    }

    public static class MediaLibrary {
        public List<User> Users;
        public List<Media> Media;
        public List<Rating> Ratings;
    }

    public void addMedia(Media media) {
        mediaList.add(media);
    }

    public void deleteMedia(UUID uuid) {
        for (Media media : mediaList) {
            if (media.getId().equals(uuid)) {
                mediaList.remove(media);
            }
        }
    }

    public void addRating(Rating rating) {
        ratingList.add(rating);
    }

    public void addLike(Like like) {
        likesList.add(like);
    }

    public void deleteLike(UUID uuid) {
        for (Like like : likesList) {}
    }

    public List<Like> getLikes() {
        return likesList;
    }

    public boolean addUser(User user) {
        boolean result = true;
        for (User u : userList) {
            if (u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())) {
                result = false;
            }
        }

        if (result) {
            userList.add(user);
        }

        return result;
    }

    public void deleteUser(User user) {
        userList.remove(user);
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

    public List<Rating> getRatingsOfUser(UUID uuid) {
        List<Rating> ratings = new ArrayList<>();
        for (Rating rating : ratingList) {
            if (rating.getUserId().equals(uuid)) {
                ratings.add(rating);
            }
        }
        return ratings;
    }

    public String getAllData() {
        StringBuilder sb = new StringBuilder();
        for (User user : userList) {
            sb.append(user.toJson());
        }
        for (Media media : mediaList) {
            sb.append(media.toJson());
        }
        for (Rating rating : ratingList) {
            sb.append(rating.toJson());
        }
        return sb.toString();
    }

    public String getUserData() {
        StringBuilder sb = new StringBuilder();

        for (User user : userList) {
            sb.append(user.toJson());
        }

        return sb.toString();
    }

    public String getMediaData() {
        StringBuilder sb = new StringBuilder();

        for (Media media : mediaList) {
            sb.append(media.toJson());
        }

        return sb.toString();
    }

    public String getRatingData() {
        StringBuilder sb = new StringBuilder();

        for (Rating rating : ratingList) {
            sb.append(rating.toJson());
        }

        return sb.toString();
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    public List<String> getTokenList() {
        return tokenList;
    }

    public String getToken(UUID uuid) {
        JWTUtil util = new JWTUtil();
        String resp = "";

        for (String token : tokenList) {
            if (util.getUserIdFromToken(token).equals(uuid.toString())) {
                resp = token;
            }
        }

        return resp;
    }

    public void setTokenList(List<String> tokenList) {
        this.tokenList = tokenList;
    }

    public void addToken(String token) {
        tokenList.add(token);
    }

    public void removeToken(String token) {
        tokenList.remove(token);
    }

    public User getUser(UUID uuid) {
        User retUser = null;
        for (User user : userList) {
            if (user.getId().equals(uuid)) {
                retUser = user;
            }
        }
        return retUser;
    }
}
