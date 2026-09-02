package ru.practicum.moviehub.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.store.MoviesStore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.moviehub.http.BaseHttpHandler.CONTENT_TYPE_JSON;
import static ru.practicum.moviehub.http.BaseHttpHandler.DEFAULT_CHARSET;
import static ru.practicum.moviehub.http.HttpStatus.*;

public class MoviesApiPostTest {
    private static final String BASE = "http://localhost:8080";
    private static MoviesServer server;
    private static HttpClient client;
    private static MoviesStore store;

    @BeforeAll
    static void beforeAll() throws Exception {
        store = new MoviesStore();
        server = new MoviesServer(store, 8080);
        server.start();

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

    private String getHeader(HttpResponse<String> response) {
        return response.headers().firstValue("Content-Type").orElse("");
    }

    @Test
    void postMoviesCreateNewMovie() throws Exception {
        String json = "{\"title\": \"Начало\", \"year\": 2010}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(CREATED.getStatusCode(), resp.statusCode());
        assertEquals(CONTENT_TYPE_JSON, getHeader(resp));
        assertTrue(resp.body().contains("\"title\":\"Начало\""));
        assertTrue(resp.body().contains("\"year\":2010"));
        assertTrue(resp.body().contains("\"id\""));
    }

    @Test
    void postMoviesEmptyTitle() throws Exception {
        String json = "{\"title\": \"\", \"year\": 2010}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(UNPROCESSABLE_ENTITY.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }

    @Test
    void postMoviesLongTitle() throws Exception {
        String longTitle = "a".repeat(101);
        String json = "{\"title\": \"" + longTitle + "\", \"year\": 2010}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(UNPROCESSABLE_ENTITY.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }

    @Test
    void postMoviesEarlyYear() throws Exception {
        String json = "{\"title\": \"Начало\", \"year\": 1800}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(UNPROCESSABLE_ENTITY.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }

    @Test
    void postMoviesLaterYear() throws Exception {
        String json = "{\"title\": \"Начало\", \"year\": 2100}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(UNPROCESSABLE_ENTITY.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }

    @Test
    void postMoviesIncorrectContentType() throws Exception {
        String json = "{\"title\": \"Начало\", \"year\": 2010}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(UNSUPPORTED_MEDIA_TYPE.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }

    @Test
    void postMoviesBadRequest() throws Exception {
        String json = "{\"title\": \"Начало\", \"year\": \"year\"}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        assertEquals(BAD_REQUEST.getStatusCode(), resp.statusCode());
        assertTrue(resp.body().contains("details"));
    }

}
