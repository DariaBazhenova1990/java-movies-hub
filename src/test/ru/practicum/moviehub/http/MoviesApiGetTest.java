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

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.moviehub.http.BaseHttpHandler.CONTENT_TYPE_JSON;
import static ru.practicum.moviehub.http.BaseHttpHandler.DEFAULT_CHARSET;
import static ru.practicum.moviehub.http.HttpStatus.*;

public class MoviesApiGetTest {
    private static final String BASE = "http://localhost:8080";
    private static MoviesServer server;
    private static HttpClient client;
    private static MoviesStore store;

    @BeforeAll
    static void beforeAll() throws Exception {
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
    void beforeEach() throws Exception {
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
    void getMoviesEmptyResponse() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(OK.getStatusCode(), resp.statusCode());
        assertEquals(CONTENT_TYPE_JSON, getHeader(resp));
        assertEquals("[]", resp.body());
    }

    @Test
    void getAllMovies() throws Exception {
        store.addMovie("Начало", 2010);
        store.addMovie("Одиссея", 2026);

        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(OK.getStatusCode(), resp.statusCode());
        assertEquals(CONTENT_TYPE_JSON, getHeader(resp));
        assertTrue(resp.body().contains("Одиссея"));
    }

    @Test
    void getMoviesByYear() throws Exception {
        store.addMovie("Начало", 2010);
        store.addMovie("Одиссея", 2026);

        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies?year=2010"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(OK.getStatusCode(), resp.statusCode());
        assertEquals(CONTENT_TYPE_JSON, getHeader(resp));
        assertTrue(resp.body().contains("Начало"));
    }

    @Test
    void getMovieById() throws Exception {
        store.addMovie("Начало", 2010);
        store.addMovie("Одиссея", 2026);

        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/2"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(OK.getStatusCode(), resp.statusCode());
        assertEquals(CONTENT_TYPE_JSON, getHeader(resp));
        assertTrue(resp.body().contains("Одиссея"));
    }

    @Test
    void getMovieNotFoundById() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/999"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(NOT_FOUND.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }

    @Test
    void getMovieByIncorrectId() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/abc"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(BAD_REQUEST.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }


    @Test
    void getMoviesByEmptyYear() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies?year=1900"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(OK.getStatusCode(), resp.statusCode());
        assertEquals(CONTENT_TYPE_JSON, getHeader(resp));
        assertEquals("[]", resp.body());
    }

    @Test
    void getMoviesByIncorrectYear() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies?year=abc"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(BAD_REQUEST.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }
}
