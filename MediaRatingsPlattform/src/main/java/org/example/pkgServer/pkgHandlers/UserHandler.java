package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgController.UserController;
import org.example.pkgService.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserHandler extends BaseHandler implements HttpHandler {
    private final UserController controller;

    public UserHandler(Service service) {
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

        if (path.equals("/api/users") && method.equals("GET")) {
            result = controller.getAllUsers();

        } else if (path.equals("/api/users/register") && method.equals("POST")) {
            result = controller.registerUser(body);

        } else if (path.equals("/api/users/login") && method.equals("POST")) {
            result = controller.loginUser(body);

        } else if (path.matches("^/api/users/[0-9a-fA-F-]+/profile$")) {
            UUID id = extractUUID(path);
            if (method.equals("GET")) {
                result = controller.getUserProfile(id);
            } else if (method.equals("PUT")) {
                result = controller.updateUserProfile(id, body);
            } else {
                result.put("response", "{\"error\":\"Method Not Allowed\"}");
                result.put("status", 405);
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

        int statusCode = (int) result.get("statusCode");
        String response = result.get("response").toString();

        sendResponse(exchange, statusCode, response);
    }
}
