package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgController.RatingController;
import org.example.pkgService.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RatingHandler extends BaseHandler implements HttpHandler {
    private final RatingController controller;

    public RatingHandler(Service service) {
        this.controller = new RatingController(service);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        Map<String, Object> result = new HashMap<>();

        if (path.matches("^/api/ratings/[0-9a-fA-F-]+$") && method.equals("PUT")) {
            result = controller.updateRating(extractUUID(path), body);

        } else if (path.matches("^/api/ratings/[0-9a-fA-F-]+/like$") && method.equals("POST")) {
            result = controller.likeRating(extractUUID(path), body);

        } else if (path.matches("^/api/ratings/[0-9a-fA-F-]+/confirm$") && method.equals("POST")) {
            result = controller.confirmRating(extractUUID(path));

        } else {
            result.put("response", "{\"error\":\"Not Found\"}");
            result.put("statusCode", 404);
        }

        int statusCode = (int) result.get("statusCode");
        String response = result.get("response").toString();

        sendResponse(exchange, statusCode, response);
    }
}
