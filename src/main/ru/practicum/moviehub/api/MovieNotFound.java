package ru.practicum.moviehub.api;

public class MovieNotFound extends RuntimeException {
    private final String message;

    public MovieNotFound(String id) {
        super(id);
        this.message = "Id запрашиваемого фильма = '" + id + "'";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
