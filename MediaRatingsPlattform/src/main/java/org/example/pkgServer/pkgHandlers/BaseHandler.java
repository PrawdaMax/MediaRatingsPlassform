package org.example.pkgServer.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.example.pkgServer.pkgToken.JWTUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class BaseHandler {
    JWTUtil  jwtUtil = new JWTUtil();

    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2)
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
        }
        return map;
    }

    protected UUID extractUUID(String path) {
        for (String part : path.split("/")) {
            try {
                return UUID.fromString(part);
            } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }

    public Boolean getJWTConfirm(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (!jwtUtil.isTokenExpired(token)) {
                return true;
            }
        }

        return false;
    }
}