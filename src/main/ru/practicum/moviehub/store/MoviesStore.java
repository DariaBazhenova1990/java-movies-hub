package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MoviesStore {
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final Map<Integer, Movie> movies = new ConcurrentHashMap<>();

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }

    public Movie getMovieById(int id) {
        return movies.get(id);
    }

    public void addMovie(String title, int year) {
        int id = idCounter.getAndIncrement();
        Movie movie = new Movie(id, title, year);
        movies.put(id, movie);
    }

    public boolean deleteMovie(int id) {
        return movies.remove(id) != null;
    }

    public List<Movie> getMoviesByYear(int year) {
        return movies.values().stream()
                .filter(movie -> movie.getYear() == year)
                .collect(Collectors.toList());
    }

}
