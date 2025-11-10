package org.example.pkgController;

import org.example.pkgService.Service;
import java.util.Map;
import java.util.UUID;

public class UserController {
    private final Service service;

    public UserController(Service service) {
        this.service = service;
    }

    public Map<String, Object> getAllUsers() { return service.getAllUsers(); }
    public Map<String, Object> registerUser(String body) { return service.userRegister(body); }
    public Map<String, Object> loginUser(String body) { return service.userLogin(body); }
    public Map<String, Object> getUserProfile(UUID id) { return service.getUser(id); }
    public Map<String, Object> updateUserProfile(UUID id, String body) { return service.updateUser(id, body); }
    public Map<String, Object> getUserRatings(UUID id) { return service.getUserRatings(id); }
    public Map<String, Object> getUserFavorites(UUID id) { return service.getUserFavorites(id); }
    public Map<String, Object> getUserRecommendations(UUID id, Map<String, String> query) { return service.getUserRecs(id, query); }
}
