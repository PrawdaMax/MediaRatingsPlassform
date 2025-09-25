package org.example.pkgDB;

import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private List<Media> mediaList = new ArrayList<>();
    private List<Rating> ratingList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    
    public Database(){
        userList.add(new User("alice", "12345"));
        userList.add(new User("bob", "12345"));
        userList.add(new User("carol", "12345"));
        userList.add(new User("dave", "12345"));
        userList.add(new User("eve", "12345"));

        mediaList.add(new Media("Inception", "A mind-bending thriller about dream invasion.",
                MediaType.movie, 2010, Arrays.asList("Sci-Fi", "Thriller"), 13));

        mediaList.add(new Media("Breaking Bad", "A chemistry teacher turns to drug dealing.",
                MediaType.series, 2008, Arrays.asList("Crime", "Drama"), 18));

        mediaList.add(new Media("The Witcher 3", "A fantasy RPG with rich storytelling.",
                MediaType.game, 2015, Arrays.asList("RPG", "Fantasy"), 18));

        mediaList.add(new Media("The Office", "A mockumentary-style sitcom.",
                MediaType.series, 2005, Arrays.asList("Comedy"), 12));

        mediaList.add(new Media("The Matrix", "A hacker discovers reality is a simulation.",
                MediaType.movie, 1999, Arrays.asList("Action", "Sci-Fi"), 16));

        mediaList.add(new Media("God of War", "A Spartan warrior battles gods and monsters.",
                MediaType.game, 2018, Arrays.asList("Action", "Adventure"), 18));

        mediaList.add(new Media("Friends", "Six friends navigate life in New York.",
                MediaType.series, 1994, Arrays.asList("Comedy", "Romance"), 12));

        mediaList.add(new Media("Interstellar", "A journey through space and time.",
                MediaType.movie, 2014, Arrays.asList("Sci-Fi", "Drama"), 13));

        mediaList.add(new Media("Cyberpunk 2077", "A futuristic open-world RPG.",
                MediaType.game, 2020, Arrays.asList("RPG", "Sci-Fi"), 18));

        mediaList.add(new Media("The Crown", "A biographical story about the reign of Queen Elizabeth II.",
                MediaType.series, 2016, Arrays.asList("Drama", "History"), 15));

        ratingList.add(new Rating(5, "Amazing concept!", "2025-09-20T14:00:00Z", "alice", "Inception"));
        ratingList.add(new Rating(4, "Really good drama series.", "2025-09-21T11:00:00Z", "alice", "Breaking Bad"));

        ratingList.add(new Rating(5, "Fantastic game!", "2025-09-22T10:00:00Z", "bob", "The Witcher 3"));
        ratingList.add(new Rating(3, "A bit slow for me.", "2025-09-23T09:00:00Z", "bob", "The Crown"));

        ratingList.add(new Rating(4, "Hilarious series!", "2025-09-20T16:00:00Z", "carol", "The Office"));
        ratingList.add(new Rating(5, "Still relevant and exciting.", "2025-09-24T15:00:00Z", "carol", "The Matrix"));

        ratingList.add(new Rating(5, "Action-packed masterpiece.", "2025-09-21T12:00:00Z", "dave", "God of War"));
        ratingList.add(new Rating(4, "Funny and nostalgic.", "2025-09-22T13:00:00Z", "dave", "Friends"));

        ratingList.add(new Rating(5, "Mind-blowing visuals and story.", "2025-09-23T14:00:00Z", "eve", "Interstellar"));
        ratingList.add(new Rating(4, "Great potential, still buggy.", "2025-09-24T17:00:00Z", "eve", "Cyberpunk 2077"));
    }

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
