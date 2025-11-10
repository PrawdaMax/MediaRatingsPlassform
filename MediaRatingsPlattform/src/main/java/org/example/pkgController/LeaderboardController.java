package org.example.pkgController;

import org.example.pkgService.Service;
import java.util.Map;

public class LeaderboardController {
    private final Service service;

    public LeaderboardController(Service service) {
        this.service = service;
    }

    public Map<String, Object> getLeaderboard() { return service.getLeaderboard(); }
}
