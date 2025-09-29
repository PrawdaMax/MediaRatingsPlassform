package org.example.pkgObj;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
