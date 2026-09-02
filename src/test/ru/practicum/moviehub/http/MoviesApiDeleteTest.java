package ru.practicum.moviehub.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.store.MoviesStore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.moviehub.http.BaseHttpHandler.DEFAULT_CHARSET;
import static ru.practicum.moviehub.http.HttpStatus.*;

public class MoviesApiDeleteTest {
    private static final String BASE = "http://localhost:8080";
    private static MoviesServer server;
    private static HttpClient client;
    private static MoviesStore store;

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @AfterAll
    static void afterAll() {
        if (server != null) {
            server.stop();
        }
    }

    @BeforeEach
    void beforeEach() {
        if (server != null) {
            server.stop();
        }
        store = new MoviesStore();
        server = new MoviesServer(store, 8080);
        server.start();
    }

    private String getHeader(HttpResponse<String> response) {
        return response.headers().firstValue("Content-Type").orElse("");
    }

    @Test
    void deleteMovie() throws Exception {
        store.addMovie("Начало", 2010);
        store.addMovie("Одиссея", 2026);

        HttpRequest delReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/2"))
                .DELETE()
                .build();
        HttpResponse<String> delResp = client.send(delReq, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(NO_CONTENT.getStatusCode(), delResp.statusCode());

        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/2"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(NOT_FOUND.getStatusCode(), resp.statusCode());
    }

    @Test
    void deleteMovieNotFound() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/999"))
                .DELETE()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(NOT_FOUND.getStatusCode(), resp.statusCode());
    }

    @Test
    void deleteMovieByIncorrectId() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/abc"))
                .DELETE()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(BAD_REQUEST.getStatusCode(), resp.statusCode());
    }
}
