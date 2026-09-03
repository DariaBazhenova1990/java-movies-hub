package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.api.*;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.moviehub.http.HttpStatus.*;

public class MoviesHandler extends BaseHttpHandler {
    private final MoviesStore moviesStore;
    private final Gson gson = new GsonBuilder()
            .create();

    public MoviesHandler(MoviesStore moviesStore) {
        this.moviesStore = moviesStore;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if (path.contains("?")) {
            path = path.split("\\?")[0];
        }
        try {
            switch (method) {
                case "GET":
                    routeGetRequests(exchange, path);
                    break;
                case "POST":
                    handlePostMovie(exchange);
                    break;
                case "DELETE":
                    handleDeleteMovie(exchange, path);
                    break;
                default:
                    throw new MethodNotAllowed(method);
            }
        } catch (MovieNotFound exception) {
            sendError(exchange, NOT_FOUND.getStatusCode(), NOT_FOUND.getErrorMessage(), exception.getMessage());
        } catch (BadRequest exception) {
            sendError(exchange, BAD_REQUEST.getStatusCode(), BAD_REQUEST.getErrorMessage(), exception.getMessage());
        } catch (MethodNotAllowed exception) {
            sendError(exchange, METHOD_NOT_ALLOWED.getStatusCode(), METHOD_NOT_ALLOWED.getErrorMessage(),
                    exception.getMessage());
        } catch (Exception exception) {
            sendError(exchange, INTERNAL_SERVER_ERROR.getStatusCode(), INTERNAL_SERVER_ERROR.getErrorMessage(),
                    exception.getMessage());
        }
    }

    private void routeGetRequests(HttpExchange exchange, String path) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        try {
            if (path.equals("/movies") && query != null) {
                Map<String, String> queryParams = parseQuery(query);
                if (queryParams.containsKey("year")) {
                    String yearStr = queryParams.get("year");
                    if (!yearStr.matches("\\d+")) {
                        throw new BadRequest("year", yearStr);
                    }
                    int year = Integer.parseInt(yearStr);
                    handleGetMoviesByYear(exchange, year);
                    return;
                }
            }

            if (path.equals("/movies") && query == null) {
                handleGetAllMovies(exchange);
                return;
            }

            if (path.startsWith("/movies/")) {
                String idStr = path.substring("/movies/".length());
                if (!idStr.matches("\\d+")) {
                    throw new BadRequest("id", idStr);
                }

                int id = Integer.parseInt(idStr);
                handleGetMovieById(exchange, id);
            }
        } catch (MovieNotFound exception) {
            sendError(exchange, NOT_FOUND.getStatusCode(), NOT_FOUND.getErrorMessage(), exception.getMessage());
        } catch (BadRequest exception) {
            sendError(exchange, BAD_REQUEST.getStatusCode(), BAD_REQUEST.getErrorMessage(), exception.getMessage());
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private void handleGetAllMovies(HttpExchange exchange) throws IOException {
        List<Movie> movies = moviesStore.getAllMovies();
        sendJson(exchange, OK.getStatusCode(), gson.toJson(movies));
    }

    private void handleGetMovieById(HttpExchange exchange, int id) throws IOException {
        Movie movie = moviesStore.getMovieById(id);
        if (movie != null) {
            sendJson(exchange, OK.getStatusCode(), gson.toJson(movie));
        } else {
            throw new MovieNotFound(String.valueOf(id));
        }
    }

    private void handleGetMoviesByYear(HttpExchange exchange, int year) throws IOException {
        List<Movie> movies = moviesStore.getMoviesByYear(year);
        sendJson(exchange, OK.getStatusCode(), gson.toJson(movies));
    }

    private void handlePostMovie(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        try {
            if (contentType == null || !contentType.startsWith("application/json")) {
                throw new UnsupportedMediaType("Content-Type = application/json");
            }
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Movie movie = gson.fromJson(body, Movie.class);
            if (!movie.isValidTitle() || !movie.isValidYear()) {
                throw new UnprocessableEntity(movie.toString());
            }
            moviesStore.addMovie(movie.getTitle(), movie.getYear());
            sendJson(exchange, CREATED.getStatusCode(), gson.toJson(movie));
        } catch (UnsupportedMediaType exception) {
            sendError(exchange, UNSUPPORTED_MEDIA_TYPE.getStatusCode(), UNSUPPORTED_MEDIA_TYPE.getErrorMessage(),
                    exception.getMessage());
        } catch (JsonSyntaxException | BadRequest exception) {
            sendError(exchange, BAD_REQUEST.getStatusCode(), BAD_REQUEST.getErrorMessage(), exception.getMessage());
        } catch (UnprocessableEntity exception) {
            sendError(exchange, UNPROCESSABLE_ENTITY.getStatusCode(), UNPROCESSABLE_ENTITY.getErrorMessage(),
                    exception.getMessage());
        }
    }

    private void handleDeleteMovie(HttpExchange exchange, String path) throws IOException {
        if (!path.matches("^/movies/\\d+$")) {
            throw new BadRequest("path", path);
        }
        String movieId = path.substring("/movies/".length());
        int id = Integer.parseInt(movieId);
        boolean deleted = moviesStore.deleteMovie(id);
        if (deleted) {
            sendNoContent(exchange);
        } else {
            throw new MovieNotFound(String.valueOf(id));
        }
    }

    private void sendError(HttpExchange exchange,
                           int statusCode,
                           String errorMessage,
                           String details) throws IOException {
        sendJson(exchange, statusCode, gson.toJson(new ErrorResponse(errorMessage, details)));
    }

}
