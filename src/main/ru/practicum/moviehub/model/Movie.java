package ru.practicum.moviehub.model;

import java.time.Year;

public class Movie {
    public static final int MIN_ALLOWED_YEAR = 1888;
    public static final int MAX_ALLOWED_YEAR = Year.now().getValue() + 1;
    public static final int MAX_TITLE_LENGTH = 100;

    private final int id;
    private final String title;
    private final int year;

    public Movie(int id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                '}';
    }

    public boolean isValidTitle() {
        return !title.isEmpty() && title.length() <= MAX_TITLE_LENGTH;
    }

    public boolean isValidYear() {
        return year >= MIN_ALLOWED_YEAR && year <= MAX_ALLOWED_YEAR;
    }

}
