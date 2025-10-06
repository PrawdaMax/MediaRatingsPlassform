package org.example.pkgService;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.pkgService.pkgHandlers.ApiHandler;
import org.example.pkgService.pkgHandlers.LoginHandler;
import org.example.pkgService.pkgHandlers.RegisterHandler;
import org.example.pkgService.pkgHandlers.RootHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

//Task1 (Alles in der start function)

public class Server {
    private final int port;
    private final Service service;
    private HttpServer httpServer;

    public Server(int port, Service service) {
        this.port = port;
        this.service = service;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        registerContexts(httpServer);
        httpServer.setExecutor(null);
        httpServer.start();
        logStartup();
    }

    private void registerContexts(HttpServer server) {
        server.createContext("/", new RootHandler());
        server.createContext("/api", new ApiHandler(service));
        server.createContext("/api/login", new LoginHandler(service));
        server.createContext("/api/register", new RegisterHandler(service));
    }

    private void logStartup() {
        System.out.println("Server started on port " + port);
    }
}
