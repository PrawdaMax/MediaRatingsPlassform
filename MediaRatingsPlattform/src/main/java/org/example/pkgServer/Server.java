package org.example.pkgServer;

import com.sun.net.httpserver.HttpServer;
import org.example.pkgServer.pkgHandlers.*;
import org.example.pkgService.Service;
import org.example.pkgUI.AppLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Server {
    private final int port;
    private final Service service;
    private HttpServer httpServer;
    private final Logger log = (Logger) AppLogger.getLogger(Server.class);

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
        server.createContext("/api/users", new UserHandler(service));
        server.createContext("/api/media", new MediaHandler(service));
        server.createContext("/api/ratings", new RatingHandler(service));
        server.createContext("/api/leaderboard", new LeaderboardHandler(service));
    }

    private void logStartup() {
        log.info("Server started on port " + port);
    }
}
