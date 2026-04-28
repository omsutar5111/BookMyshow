package com.bookmyshow;

import com.bookmyshow.model.Movie;
import com.bookmyshow.model.User;
import com.bookmyshow.repository.MovieRepository;
import com.bookmyshow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BookMyShowApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void contextLoads() {
        assertThat(userRepository).isNotNull();
        assertThat(movieRepository).isNotNull();
    }

    @Test
    void canSaveAndFindUser() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("encoded_password")
                .phone("1234567890")
                .role(User.Role.USER)
                .build();

        User saved = userRepository.save(user);
        assertThat(saved.getId()).isNotNull();

        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");

        userRepository.delete(saved);
    }

    @Test
    void canSaveAndFindMovie() {
        Movie movie = Movie.builder()
                .title("Test Movie")
                .genre("Action")
                .durationMinutes(120)
                .releaseDate(LocalDate.now().plusDays(10))
                .language("English")
                .status(Movie.MovieStatus.UPCOMING)
                .build();

        Movie saved = movieRepository.save(movie);
        assertThat(saved.getId()).isNotNull();

        List<Movie> upcoming = movieRepository.findByStatus(Movie.MovieStatus.UPCOMING);
        assertThat(upcoming).isNotEmpty();

        List<Movie> searchResult = movieRepository.findByTitleContainingIgnoreCase("test");
        assertThat(searchResult).isNotEmpty();

        movieRepository.delete(saved);
    }
}
