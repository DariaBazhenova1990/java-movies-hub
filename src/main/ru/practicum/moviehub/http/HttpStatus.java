package ru.practicum.moviehub.http;

enum HttpStatus {
    OK(200, null),
    CREATED(201, null),
    NO_CONTENT(204, null),
    BAD_REQUEST(400, "Неверный запрос"),
    NOT_FOUND(404, "Фильм не найден"),
    METHOD_NOT_ALLOWED(405, "Метод не поддерживается"),
    UNSUPPORTED_MEDIA_TYPE(415, "Неправильное значение заголовка"),
    UNPROCESSABLE_ENTITY(422, "Ошибка валидации"),
    INTERNAL_SERVER_ERROR(500, "Внутренняя ошибка сервера");

    private final int statusCode;
    private final String errorMessage;

    HttpStatus(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return statusCode + " " + errorMessage;
    }
}

