package org.example.pkgService.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgService.Service;

import java.io.IOException;
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

        Map<String, String> queryParams = parseQueryParams(query);

        String response;
        int statusCode = 200;

        switch (path) {
            case "/api":
                response = service.getAllData();
                break;
            case "/api/Media":
                response = service.getQueryMediaData(queryParams);
                break;
            case "/api/User":
                response = service.getQueryUserData(queryParams);
                break;
            case "/api/Rating":
                response = service.getQueryRatingData(queryParams);
                break;
            default:
                response = "{\"error\":\"Not Found\"}";
                statusCode = 404;
                break;
        }

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
