package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static ru.practicum.moviehub.http.HttpStatus.NO_CONTENT;

abstract class BaseHttpHandler implements HttpHandler {
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int NO_RESPONSE_BODY = -1;

    protected void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] response = json.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_JSON);
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNoContent(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(NO_CONTENT.getStatusCode(), NO_RESPONSE_BODY);
    }
}