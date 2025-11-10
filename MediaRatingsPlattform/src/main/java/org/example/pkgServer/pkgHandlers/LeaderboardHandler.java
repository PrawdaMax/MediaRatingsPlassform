package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgController.LeaderboardController;
import org.example.pkgService.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardHandler extends BaseHandler implements HttpHandler {
    private final LeaderboardController controller;

    public LeaderboardHandler(Service service) {
        this.controller = new LeaderboardController(service);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();

        if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().equals("/api/leaderboard")) {
            result = controller.getLeaderboard();
        } else {
            result.put("response", "{\"error\":\"Not Found\"}");
            result.put("statusCode", 404);
        }

        int statusCode = (int) result.get("statusCode");
        String response = result.get("response").toString();

        sendResponse(exchange, statusCode, response);
    }
}
