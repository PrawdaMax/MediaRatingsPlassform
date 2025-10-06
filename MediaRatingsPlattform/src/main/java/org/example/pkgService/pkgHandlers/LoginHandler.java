package org.example.pkgService.pkgHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.pkgService.Service;

import java.io.IOException;

public class LoginHandler implements HttpHandler {
    private Service service;

    public LoginHandler(Service service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = exchange.getRequestBody().toString();
    }
}
