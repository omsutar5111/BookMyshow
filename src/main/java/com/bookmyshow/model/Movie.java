package com.bookmyshow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String genre;

    private Integer durationMinutes;

    private LocalDate releaseDate;

    private String language;

    private Double rating;

    private String posterUrl;

    private String trailerUrl;

    private String director;

    @Column(name = "cast_members")
    private String cast;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MovieStatus status = MovieStatus.UPCOMING;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Show> shows = new ArrayList<>();

    public enum MovieStatus {
        UPCOMING, NOW_SHOWING, ENDED
    }
}
