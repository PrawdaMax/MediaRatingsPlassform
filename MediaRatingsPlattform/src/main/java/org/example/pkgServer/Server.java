package org.example.pkgServer;

import com.sun.net.httpserver.HttpServer;
import org.example.pkgServer.pkgHandlers.*;
import org.example.pkgService.LeaderboardService;
import org.example.pkgService.MediaService;
import org.example.pkgService.RatingService;
import org.example.pkgService.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Server {
    private final int port;
    private UserService userService;
    private RatingService ratingService;
    private MediaService mediaService;
    private LeaderboardService leaderboardService;
    private HttpServer httpServer;
    //private final Logger log = AppLogger.getLogger(Server.class);

    public Server(int port, UserService userService, MediaService mediaService, RatingService ratingService, LeaderboardService leaderService) {
        this.port = port;
        this.userService = userService;
        this.mediaService = mediaService;
        this.ratingService = ratingService;
        this.leaderboardService = leaderService;
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
        server.createContext("/api/users", new UserHandler(userService));
        server.createContext("/api/media", new MediaHandler(mediaService));
        server.createContext("/api/ratings", new RatingHandler(ratingService));
        server.createContext("/api/leaderboard", new LeaderboardHandler(leaderboardService));
        server.createContext("/api/auth", new AuthHandler(userService));
    }

    private void logStartup() {
        System.out.println("Starting Server on port " + port);
        //log.info("Server started on port " + port);
    }
}
