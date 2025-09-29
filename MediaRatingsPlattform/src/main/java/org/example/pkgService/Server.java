package org.example.pkgService;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.pkgService.pkgHandlers.ApiHandler;
import org.example.pkgService.pkgHandlers.RootHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {
    private int PORT = 8080;
    private Service service;

    public Server(int port, Service sv) throws IOException {
        this.PORT = port;
        this.service = sv;
    }

    public void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        //http://localhost:8000/

        server.createContext("/", new RootHandler());
        server.createContext("/api", new ApiHandler(service));
        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + PORT);
    }
}
