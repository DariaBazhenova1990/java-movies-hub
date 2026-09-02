package ru.practicum.moviehub.api;

public class MethodNotAllowed extends RuntimeException {
    private final String message;

    public MethodNotAllowed(String method) {
        super(method);
        this.message = "Используется неподдерживаемый метод '" + method + "'";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
