package ru.practicum.moviehub.api;

public class UnsupportedMediaType extends RuntimeException {
    private final String message;

    public UnsupportedMediaType(String message) {
        super(message);
        this.message = "Ожидается " + message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
