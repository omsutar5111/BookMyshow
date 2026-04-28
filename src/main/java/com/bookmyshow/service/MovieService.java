package com.bookmyshow.service;

import com.bookmyshow.dto.MovieRequest;
import com.bookmyshow.exception.ResourceNotFoundException;
import com.bookmyshow.model.Movie;
import com.bookmyshow.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getMoviesByStatus(Movie.MovieStatus status) {
        return movieRepository.findByStatus(status);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    public List<Movie> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenreIgnoreCase(genre);
    }

    public List<Movie> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguageIgnoreCase(language);
    }

    @Transactional
    public Movie createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .durationMinutes(request.getDurationMinutes())
                .releaseDate(request.getReleaseDate())
                .language(request.getLanguage())
                .rating(request.getRating())
                .posterUrl(request.getPosterUrl())
                .trailerUrl(request.getTrailerUrl())
                .director(request.getDirector())
                .cast(request.getCast())
                .status(request.getStatus() != null ? request.getStatus() : Movie.MovieStatus.UPCOMING)
                .build();
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(Long id, MovieRequest request) {
        Movie movie = getMovieById(id);
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setLanguage(request.getLanguage());
        movie.setRating(request.getRating());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        if (request.getStatus() != null) {
            movie.setStatus(request.getStatus());
        }
        return movieRepository.save(movie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }
}
