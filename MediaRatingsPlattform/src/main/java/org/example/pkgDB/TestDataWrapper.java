package org.example.pkgDB;

import com.google.gson.annotations.SerializedName;
import org.example.pkgMisc.MediaType;

import java.util.List;

public class TestDataWrapper {

    @SerializedName("Users")
    public List<UserJson> users;

    @SerializedName("Media")
    public List<MediaJson> media;

    @SerializedName("Ratings")
    public List<RatingJson> ratings;

    // Inner DTO classes for Gson mapping

    public static class UserJson {
        @SerializedName("id")
        public String id;

        @SerializedName("username")
        public String username;

        @SerializedName("password")
        public String password;

        @SerializedName("favorites")
        public List<String> favorites;

        @SerializedName("favoriteGenres")
        public List<String> favoriteGenres;
    }

    public static class MediaJson {
        @SerializedName("id")
        public String id;

        @SerializedName("title")
        public String title;

        @SerializedName("description")
        public String description;

        @SerializedName("mediaType")
        public String mediaType;

        @SerializedName("year")
        public int year;

        @SerializedName("genres")
        public List<String> genres;

        @SerializedName("ageRestriction")
        public int ageRestriction;
    }

    public static class RatingJson {
        @SerializedName("id")
        public String id;

        @SerializedName("value")
        public int value;

        @SerializedName("comment")
        public String comment;

        @SerializedName("timestamp")
        public String timestamp;

        @SerializedName("userId")
        public String userId;

        @SerializedName("mediaId")
        public String mediaId;

        @SerializedName("confirmed")
        public boolean confirmed;
    }
}
