package org.example.pkgObj;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.Gson;
import org.example.pkgMisc.MediaType;

import java.util.List;
import java.util.UUID;

public class Media {
    private UUID id;
    private String title;
    private String description;
    private MediaType mediaType;
    private int year;
    private List<String> genres;
    private int ageRestriction;
    private List<Rating> ratings = null;

    public Media(String title, String description, MediaType mediaType, int year, List<String> genres, int ageRestriction) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.year = year;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getTitle() {
        return title;
    }
}
