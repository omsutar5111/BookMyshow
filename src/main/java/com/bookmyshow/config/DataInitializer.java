package com.bookmyshow.config;

import com.bookmyshow.model.*;
import com.bookmyshow.repository.*;
import com.bookmyshow.service.TheaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Data already initialized, skipping...");
            return;
        }

        logger.info("Initializing sample data...");

        // Create admin user
        User admin = User.builder()
                .name("Admin User")
                .email("admin@bookmyshow.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("9999999999")
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(admin);

        // Create regular user
        User user = User.builder()
                .name("John Doe")
                .email("user@bookmyshow.com")
                .password(passwordEncoder.encode("user123"))
                .phone("8888888888")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        // Create movies
        Movie movie1 = Movie.builder()
                .title("Avengers: Endgame")
                .description("The Avengers assemble for one final battle against Thanos.")
                .genre("Action/Sci-Fi")
                .durationMinutes(181)
                .releaseDate(LocalDate.now().plusDays(7))
                .language("English")
                .rating(8.4)
                .posterUrl("https://example.com/avengers-endgame.jpg")
                .director("Anthony & Joe Russo")
                .cast("Robert Downey Jr., Chris Evans, Scarlett Johansson")
                .status(Movie.MovieStatus.UPCOMING)
                .build();
        movieRepository.save(movie1);

        Movie movie2 = Movie.builder()
                .title("The Dark Knight")
                .description("Batman faces the Joker in a battle for Gotham City's soul.")
                .genre("Action/Crime")
                .durationMinutes(152)
                .releaseDate(LocalDate.now().minusDays(5))
                .language("English")
                .rating(9.0)
                .posterUrl("https://example.com/dark-knight.jpg")
                .director("Christopher Nolan")
                .cast("Christian Bale, Heath Ledger, Aaron Eckhart")
                .status(Movie.MovieStatus.NOW_SHOWING)
                .build();
        movieRepository.save(movie2);

        Movie movie3 = Movie.builder()
                .title("Pathaan")
                .description("An exiled spy returns to save India from a terrorist plot.")
                .genre("Action/Thriller")
                .durationMinutes(146)
                .releaseDate(LocalDate.now().minusDays(3))
                .language("Hindi")
                .rating(5.9)
                .posterUrl("https://example.com/pathaan.jpg")
                .director("Siddharth Anand")
                .cast("Shah Rukh Khan, Deepika Padukone, John Abraham")
                .status(Movie.MovieStatus.NOW_SHOWING)
                .build();
        movieRepository.save(movie3);

        Movie movie4 = Movie.builder()
                .title("Interstellar")
                .description("A team of explorers travel through a wormhole in space to save humanity.")
                .genre("Sci-Fi/Drama")
                .durationMinutes(169)
                .releaseDate(LocalDate.now().plusDays(14))
                .language("English")
                .rating(8.6)
                .posterUrl("https://example.com/interstellar.jpg")
                .director("Christopher Nolan")
                .cast("Matthew McConaughey, Anne Hathaway, Jessica Chastain")
                .status(Movie.MovieStatus.UPCOMING)
                .build();
        movieRepository.save(movie4);

        // Create theaters
        Theater theater1 = Theater.builder()
                .name("PVR Cinemas - Phoenix Mall")
                .address("Phoenix Mall, Lower Parel")
                .city("Mumbai")
                .state("Maharashtra")
                .pincode("400013")
                .contactNumber("022-12345678")
                .build();
        theater1 = theaterRepository.save(theater1);

        Theater theater2 = Theater.builder()
                .name("INOX - Hyderabad")
                .address("GVK One Mall, Banjara Hills")
                .city("Hyderabad")
                .state("Telangana")
                .pincode("500034")
                .contactNumber("040-12345678")
                .build();
        theater2 = theaterRepository.save(theater2);

        // Add screens
        Screen screen1 = theaterService.addScreen(theater1.getId(), "Screen 1", 10, 15);
        Screen screen2 = theaterService.addScreen(theater1.getId(), "Screen 2", 8, 12);
        Screen screen3 = theaterService.addScreen(theater2.getId(), "Screen 1", 10, 15);

        // Create shows for now-showing movies
        LocalDate today = LocalDate.now();

        Show show1 = Show.builder()
                .movie(movie2)
                .screen(screen1)
                .showDate(today)
                .showTime(LocalTime.of(10, 0))
                .normalSeatPrice(new BigDecimal("200.00"))
                .premiumSeatPrice(new BigDecimal("300.00"))
                .vipSeatPrice(new BigDecimal("500.00"))
                .availableSeats(screen1.getTotalSeats())
                .status(Show.ShowStatus.SCHEDULED)
                .build();
        showRepository.save(show1);

        Show show2 = Show.builder()
                .movie(movie2)
                .screen(screen1)
                .showDate(today)
                .showTime(LocalTime.of(14, 30))
                .normalSeatPrice(new BigDecimal("200.00"))
                .premiumSeatPrice(new BigDecimal("300.00"))
                .vipSeatPrice(new BigDecimal("500.00"))
                .availableSeats(screen1.getTotalSeats())
                .status(Show.ShowStatus.SCHEDULED)
                .build();
        showRepository.save(show2);

        Show show3 = Show.builder()
                .movie(movie3)
                .screen(screen2)
                .showDate(today)
                .showTime(LocalTime.of(11, 0))
                .normalSeatPrice(new BigDecimal("180.00"))
                .premiumSeatPrice(new BigDecimal("250.00"))
                .vipSeatPrice(new BigDecimal("400.00"))
                .availableSeats(screen2.getTotalSeats())
                .status(Show.ShowStatus.SCHEDULED)
                .build();
        showRepository.save(show3);

        Show show4 = Show.builder()
                .movie(movie3)
                .screen(screen3)
                .showDate(today)
                .showTime(LocalTime.of(15, 0))
                .normalSeatPrice(new BigDecimal("180.00"))
                .premiumSeatPrice(new BigDecimal("250.00"))
                .vipSeatPrice(new BigDecimal("400.00"))
                .availableSeats(screen3.getTotalSeats())
                .status(Show.ShowStatus.SCHEDULED)
                .build();
        showRepository.save(show4);

        logger.info("Sample data initialized successfully!");
        logger.info("Admin login: admin@bookmyshow.com / admin123");
        logger.info("User login: user@bookmyshow.com / user123");
    }
}
