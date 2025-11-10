package org.example.pkgController;

import org.example.pkgService.Service;
import java.util.Map;
import java.util.UUID;

public class RatingController {
    private final Service service;

    public RatingController(Service service) {
        this.service = service;
    }

    public Map<String, Object> updateRating(UUID id, String body) { return service.updateRating(id, body); }
    public Map<String, Object> likeRating(UUID id, String body) { return service.likeRating(id, body); }
    public Map<String, Object> confirmRating(UUID id) { return service.confirmRating(id); }
}
