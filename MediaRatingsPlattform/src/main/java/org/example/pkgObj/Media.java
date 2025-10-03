package org.example.pkgObj;

import com.google.gson.Gson;
import org.example.pkgMisc.MediaType;

import java.util.List;

public class Media {
    private String title;
    private String description;
    private MediaType mediaType;
    private int year;
    private List<String> genres;
    private int ageRestriction;
    private List<Rating> ratings = null;

    public Media(String title, String description, MediaType mediaType, int year, List<String> genres, int ageRestriction) {
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.year = year;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
    }
    
    @Override
    public String toString() {
        return "Media{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", mediaType=" + mediaType +
                ", year=" + year +
                ", genres=" + genres +
                ", ageRestriction=" + ageRestriction +
                '}';
    }
    
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getTitle() {
        return title;
    }
}
