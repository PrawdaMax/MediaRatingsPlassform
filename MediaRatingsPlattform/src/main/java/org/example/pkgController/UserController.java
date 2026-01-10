package org.example.pkgController;

import org.example.pkgService.UserService;

import java.util.Map;
import java.util.UUID;

public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    public Map<String, Object> getAllUsers() { return service.getAllUsers(); }
    public Map<String, Object> registerUser(String body) { return service.registerUser(body); }
    public Map<String, Object> loginUser(String body) { return service.loginUser(body); }
    public Map<String, Object> getUserProfile(UUID id) { return service.getUserProfile(id); }
    public Map<String, Object> updateUserProfile(UUID id, String body) { return service.updateUserProfile(id, body); }
    public Map<String, Object> getUserRatings(UUID id) { return service.getUserRatings(id); }
    public Map<String, Object> getUserFavorites(UUID id) { return service.getUserFavorites(id); }
    public Map<String, Object> getUserRecommendations(UUID id, Map<String, String> query) { return service.getUserRecommendations(id, query); }
}
