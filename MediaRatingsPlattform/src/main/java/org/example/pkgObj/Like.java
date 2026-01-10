package org.example.pkgObj;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.Gson;

import java.util.UUID;

public class Like {
    private UUID id;
    private UUID userId;
    private UUID ratingId;

    public Like(UUID userId, UUID ratingId) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.userId = userId;
        this.ratingId = ratingId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getRatingId() {
        return ratingId;
    }
}
