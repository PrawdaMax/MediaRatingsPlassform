package org.example.pkgService.pkgHandlers;

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

        switch (path) {
            case "/api":
                result = service.apiData(method);
                break;
            case "/api/Media":
                result = service.getQueryMediaData(queryParams, method);
                break;
            case "/api/User":
                result = service.getQueryUserData(queryParams, method);
                break;
            case "/api/Rating":
                result = service.getQueryRatingData(queryParams, method);
                break;
            case "/api/Login":
                result = service.userLogin(body, method);
                break;
            case "/api/Register":
                result = service.userRegister(body, method);
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
}
