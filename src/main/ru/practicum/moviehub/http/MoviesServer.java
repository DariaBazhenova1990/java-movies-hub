package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MoviesServer {
    private final HttpServer server;
    private final int port;

    public MoviesServer(MoviesStore moviesStore, int port) {
        this.port = port;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/movies", new MoviesHandler(moviesStore));
        } catch (IOException exception) {
            throw new RuntimeException("Не удалось создать HTTP-сервер", exception);
        }
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }
}
