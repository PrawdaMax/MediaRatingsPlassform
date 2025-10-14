package org.example.pkgObj;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserWithRatings {
    private final User user;
    private final List<Rating> ratings;

    public UserWithRatings(User user, List<Rating> ratings) {
        this.user = user;
        this.ratings = ratings;
    }

    public User getUser() {
        return user;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
