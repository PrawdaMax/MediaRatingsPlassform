package org.example.pkgController;

import org.example.pkgService.LeaderboardService;
import java.util.Map;

public class LeaderboardController {
    private final LeaderboardService service;

    public LeaderboardController(LeaderboardService service) {
        this.service = service;
    }

    public Map<String, Object> getLeaderboard() { return service.getLeaderboard(); }
}
