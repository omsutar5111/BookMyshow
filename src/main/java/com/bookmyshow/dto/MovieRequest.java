package com.bookmyshow.dto;

import com.bookmyshow.model.Movie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String genre;

    @NotNull(message = "Duration is required")
    private Integer durationMinutes;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    private String language;

    private Double rating;

    private String posterUrl;

    private String trailerUrl;

    private String director;

    private String cast;

    private Movie.MovieStatus status;
}
