package org.example.pkgController;

import org.example.pkgService.Service;
import java.util.Map;
import java.util.UUID;

public class MediaController {
    private final Service service;

    public MediaController(Service service) {
        this.service = service;
    }

    private String get(Map<String, Object> result) {
        return (String) result.get("response");
    }

    public Map<String, Object> getAllMedia(Map<String, String> query) { return service.getAllMedia(query); }
    public Map<String, Object> postMedia(String body) { return service.postMedia(body); }
    public Map<String, Object> getMedia(UUID id) { return service.getMedia(id); }
    public Map<String, Object> updateMedia(UUID id, String body) { return service.updateMedia(id, body); }
    public Map<String, Object> deleteMedia(UUID id) { return service.deleteMedia(id); }
    public Map<String, Object> addRating(UUID id, String body) { return service.addRating(id, body); }
    public Map<String, Object> markAsFavorite(UUID id, String body) { return service.markAsFavorite(id, body); }
    public Map<String, Object> unmarkAsFavorite(UUID id, String body) { return service.unmarkAsFavorite(id, body); }
}