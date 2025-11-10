package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgController.MediaController;
import org.example.pkgService.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MediaHandler extends BaseHandler implements HttpHandler {
    private final MediaController controller;

    public MediaHandler(Service service) {
        this.controller = new MediaController(service);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> queryParams = parseQueryParams(query);

        Map<String, Object> result = new HashMap<>();

        if (path.equals("/api/media") && method.equals("GET")) {
            result = controller.getAllMedia(queryParams);

        } else if (path.equals("/api/media") && method.equals("POST")) {
            result = controller.postMedia(body);

        } else if (path.matches("^/api/media/[0-9a-fA-F-]+$")) {
            UUID id = extractUUID(path);
            switch (method) {
                case "GET":
                    result = controller.getMedia(id);
                    break;
                case "PUT":
                    result = controller.updateMedia(id, body);
                    break;
                case "DELETE":
                    result = controller.deleteMedia(id);
                    break;
                default:
                    result.put("response", "{\"error\":\"Method Not Allowed\"}");
                    result.put("status", 405);
                    break;
            }

        } else if (path.matches("^/api/media/[0-9a-fA-F-]+/rate$") && method.equals("POST")) {
            result = controller.addRating(extractUUID(path), body);

        } else if (path.matches("^/api/media/[0-9a-fA-F-]+/favorite$")) {
            UUID id = extractUUID(path);
            if (method.equals("POST")) {
                result = controller.markAsFavorite(id, body);
            } else if (method.equals("DELETE")) {
                result = controller.unmarkAsFavorite(id, body);
            } else {
                result.put("response", "{\"error\":\"Method Not Allowed\"}");
                result.put("status", 405);
            }

        } else {
            result.put("response", "{\"error\":\"Not Found\"}");
            result.put("statusCode", 404);
        }

        int statusCode = (int) result.get("statusCode");
        String response = result.get("response").toString();

        sendResponse(exchange, statusCode, response);
    }
}
