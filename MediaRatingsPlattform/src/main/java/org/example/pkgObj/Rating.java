package org.example.pkgObj;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.Gson;

import java.util.UUID;

public class Rating {
    private UUID id;
    private UUID userId;
    private UUID mediaId;
    int value;
    String comment;
    String timestamp;
    Boolean confirmed;

    public Rating(int value, String comment, String timestamp, UUID userId, UUID mediaId) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.value = value;
        this.comment = comment;
        this.userId = userId;
        this.mediaId = mediaId;
        this.confirmed = false;
    }

    public void confirm() {
        this.confirmed = true;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }
}
