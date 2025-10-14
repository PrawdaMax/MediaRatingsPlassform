package org.example.pkgObj;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.Gson;

import java.util.UUID;

public class Like {
    private UUID id;
    private UUID userId;
    private UUID ratingId;
    private String timestamp;

    public Like(UUID userId, UUID ratingId) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.userId = userId;
        this.ratingId = ratingId;
    }

    public UUID getId() {
        return id;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRatingId() {
        return ratingId;
    }

    public void setRatingId(UUID ratingId) {
        this.ratingId = ratingId;
    }
}
