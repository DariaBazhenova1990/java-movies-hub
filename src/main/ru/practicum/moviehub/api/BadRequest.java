package ru.practicum.moviehub.api;

public class BadRequest extends RuntimeException {
    private final String message;

    public BadRequest(String source, String value) {
        super(value);
        this.message = "Некорректное значение для " + source + " = '" + value + "'";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
