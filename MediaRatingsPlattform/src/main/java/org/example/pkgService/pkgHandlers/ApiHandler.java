package org.example.pkgService.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgService.Service;

import java.io.IOException;
import java.io.OutputStream;

public class ApiHandler implements HttpHandler {

    Service service;

    public ApiHandler(Service service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        String response;
        int statusCode = 200;

        switch (path) {
            case "/api":
                response = service.getAllData();
                break;
            case "/api/Media":
                response = service.getMediaData();
                break;
            case "/api/User":
                response = service.getUserData();
                break;
            case "/api/Rating":
                response = service.getRatingData();
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
}
