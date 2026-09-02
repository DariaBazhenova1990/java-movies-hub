package ru.practicum.moviehub.api;

public class ErrorResponse {
    private final String errorMessage;
    private final String details;

    public ErrorResponse(String errorMessage, String details) {
        this.errorMessage = errorMessage;
        this.details = details;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDetails() {
        return details;
    }

}
