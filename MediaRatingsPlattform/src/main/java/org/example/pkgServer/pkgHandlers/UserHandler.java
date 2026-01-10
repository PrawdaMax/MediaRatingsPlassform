package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgController.UserController;
import org.example.pkgService.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserHandler extends BaseHandler implements HttpHandler {
    private final UserController controller;

    public UserHandler(UserService service) {
        this.controller = new UserController(service);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> queryParams = parseQueryParams(query);

        Map<String, Object> result = new HashMap<>();

        if (getJWTConfirm(exchange)) {
            if (path.equals("/api/users") && method.equals("GET")) {
                result = controller.getAllUsers();

            }  else if (path.matches("^/api/users/[0-9a-fA-F-]+/profile$")) {
                UUID id = extractUUID(path);
                switch (method) {
                    case "GET" -> result = controller.getUserProfile(id);
                    case "PUT" -> result = controller.updateUserProfile(id, body);
                    default -> {
                        result.put("response", "{\"error\":\"Method Not Allowed\"}");
                        result.put("status", 405);
                    }
                }
            } else if (path.matches("^/api/users/[0-9a-fA-F-]+/ratings$") && method.equals("GET")) {
                result = controller.getUserRatings(extractUUID(path));

            } else if (path.matches("^/api/users/[0-9a-fA-F-]+/favorites$") && method.equals("GET")) {
                result = controller.getUserFavorites(extractUUID(path));

            } else if (path.matches("^/api/users/[0-9a-fA-F-]+/recommendations$") && method.equals("GET")) {
                result = controller.getUserRecommendations(extractUUID(path), queryParams);

            } else {
                result.put("response", "{\"error\":\"Not Found\"}");
                result.put("statusCode", 404);
            }
        } else {
            result.put("response", "{\"error\":\"No Valid Token\"}");
            result.put("statusCode", 401);
        }

        int statusCode = (int) result.get("statusCode");
        String response = result.get("response").toString();

        sendResponse(exchange, statusCode, response);
    }
}
