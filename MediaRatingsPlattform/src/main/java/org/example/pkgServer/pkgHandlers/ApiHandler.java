package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgService.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApiHandler implements HttpHandler {

    Service service;

    public ApiHandler(Service service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String method = exchange.getRequestMethod();
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> queryParams = parseQueryParams(query);

        Map<String, Object> result = new HashMap<>();
        String routeKey = getRouteKey(path, method);

        switch (routeKey) {
            case "GET /api/users":
                result = service.getAllUsers();
                break;
            case "POST /api/users/register":
                result = service.userRegister(body);
                break;
            case "POST /api/users/login":
                result = service.userLogin(body);
                break;
            case "GET /api/users/:id/profile":
                result = service.getUser(getUUID(path));
                break;
            case "PUT /api/users/:id/profile":
                result = service.updateUser(getUUID(path), body);
                break;
            case "GET /api/users/:id/ratings":
                result = service.getUserRatings(getUUID(path));
                break;
            case "GET /api/users/:id/favorites":
                result = service.getUserFavorites(getUUID(path));
                break;
            case "GET /api/users/:id/recommendations":
                result = service.getUserRecs(getUUID(path), queryParams);
                break;
            case "GET /api/media":
                result = service.getAllMedia(queryParams);
                break;
            case "POST /api/media":
                result = service.postMedia(body);
                break;
            case "GET /api/media/:id":
                result = service.getMedia(getUUID(path));
                break;
            case "PUT /api/media/:id":
                result = service.updateMedia(getUUID(path), body);
                break;
            case "DELETE /api/media/:id":
                result = service.deleteMedia(getUUID(path));
                break;
            case "POST /api/media/:id/rate":
                result = service.addRating(getUUID(path), body);
                break;
            case "PUT /api/ratings/:id":
                result = service.updateRating(getUUID(path), body);
                break;
            case "POST /api/ratings/:id/like":
                result = service.likeRating(getUUID(path), body);
                break;
            case "POST /api/ratings/:id/confirm":
                result = service.confirmRating(getUUID(path));
                break;
            case "POST /api/media/:id/favorite":
                result = service.markAsFavorite(getUUID(path), body);
                break;
            case "DELETE /api/media/:id/favorite":
                result = service.unmarkAsFavorite(getUUID(path), body);
                break;
            case "GET /api/leaderboard":
                result = service.getLeaderboard();
                break;
            default:
                result.put("statusCode", 404);
                result.put("response", "{\"error\":\"Not Found\"}");
                break;
        }

        String response = (String) result.get("response");
        int statusCode = (int) result.get("statusCode");

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        if (query == null) return queryPairs;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                queryPairs.put(
                        URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                );
            }
        }
        return queryPairs;
    }

    private String getRouteKey(String path, String method) {
        String[] parts = path.split("/");
        StringBuilder key = new StringBuilder(method).append(" ");
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-7[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")) {
                key.append("/:id");
            } else {
                key.append("/").append(parts[i]);
            }
        }
        return key.toString();
    }

    private UUID getUUID(String path) {
        String[] parts = path.split("/");
        UUID retId = null;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-7[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")) {
                retId = UUID.fromString(parts[i]);
                break;
            }
        }
        return retId;
    }
}
