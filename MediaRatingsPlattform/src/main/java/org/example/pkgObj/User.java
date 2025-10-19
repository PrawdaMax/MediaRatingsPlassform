package org.example.pkgObj;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String password;
    private List<UUID> favorites;
    private List<String> favoriteGenres;

    public User (String username, String password) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.password = password;
        this.username = username;
        this.favorites = new ArrayList<>();
        this.favoriteGenres = new ArrayList<>();
    }

    public void addFavoriteGenre(String genre){
        this.favoriteGenres.add(genre);
    }

    public void removeFavoriteGenre(String genre){
        this.favoriteGenres.remove(genre);
    }

    public List<String> getFavoriteGenres(){
        return this.favoriteGenres;
    }

    public void addFavorite(UUID uuid) {
        favorites.add(uuid);
    }

    public void deleteFavorite(UUID uuid) {
        favorites.remove(uuid);
    }

    public List<UUID> getFavorites() {
        return favorites;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
