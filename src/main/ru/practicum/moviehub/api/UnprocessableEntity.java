package ru.practicum.moviehub.api;

public class UnprocessableEntity extends RuntimeException {
    private final String message;

    public UnprocessableEntity(String movie) {
        super(movie);
        this.message = "Некорректная сущность: " + movie;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
