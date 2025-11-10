package org.example.pkgObj;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public record UserWithRatings(User user, List<Rating> ratings) {

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
    
    public List<Rating> getRatings() {
        return ratings;
    }
}
