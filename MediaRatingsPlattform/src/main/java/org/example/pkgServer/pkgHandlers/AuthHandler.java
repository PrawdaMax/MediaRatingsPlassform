package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgController.LeaderboardController;
import org.example.pkgController.UserController;
import org.example.pkgService.LeaderboardService;
import org.example.pkgService.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AuthHandler extends BaseHandler implements HttpHandler {
    private final UserController controller;

    public AuthHandler(UserService service) {
        this.controller = new UserController(service);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.equals("/api/auth/register") && method.equals("POST")) {
            result = controller.registerUser(body);
        } else if (path.equals("/api/auth/login") && method.equals("POST")) {
            result = controller.loginUser(body);
        } else {
            result.put("response", "{\"error\":\"Method Not Allowed\"}");
            result.put("status", 405);
        }

        int statusCode = (int) result.get("statusCode");
        String response = result.get("response").toString();

        sendResponse(exchange, statusCode, response);
    }
}
