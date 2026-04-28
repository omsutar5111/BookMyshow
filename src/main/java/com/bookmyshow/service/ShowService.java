package com.bookmyshow.service;

import com.bookmyshow.dto.ShowRequest;
import com.bookmyshow.exception.ResourceNotFoundException;
import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Show;
import com.bookmyshow.repository.MovieRepository;
import com.bookmyshow.repository.ScreenRepository;
import com.bookmyshow.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShowService {

    private static final java.math.BigDecimal PREMIUM_PRICE_MULTIPLIER = new java.math.BigDecimal("1.5");
    private static final java.math.BigDecimal VIP_PRICE_MULTIPLIER = new java.math.BigDecimal("2");

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    public List<Show> getShowsByMovie(Long movieId) {
        return showRepository.findByMovieId(movieId);
    }

    public List<Show> getShowsByMovieAndDate(Long movieId, LocalDate date) {
        return showRepository.findByMovieIdAndShowDate(movieId, date);
    }

    public List<Show> getUpcomingShowsByMovie(Long movieId) {
        return showRepository.findUpcomingShowsByMovie(movieId, LocalDate.now());
    }

    public List<Show> getShowsByCityAndMovieAndDate(String city, Long movieId, LocalDate date) {
        return showRepository.findShowsByCityAndMovieAndDate(city, movieId, date);
    }

    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
    }

    @Transactional
    public Show createShow(ShowRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.getMovieId()));

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + request.getScreenId()));

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .showDate(request.getShowDate())
                .showTime(request.getShowTime())
                .normalSeatPrice(request.getNormalSeatPrice())
                .premiumSeatPrice(request.getPremiumSeatPrice() != null
                        ? request.getPremiumSeatPrice() : request.getNormalSeatPrice().multiply(PREMIUM_PRICE_MULTIPLIER))
                .vipSeatPrice(request.getVipSeatPrice() != null
                        ? request.getVipSeatPrice() : request.getNormalSeatPrice().multiply(VIP_PRICE_MULTIPLIER))
                .availableSeats(screen.getTotalSeats())
                .status(Show.ShowStatus.SCHEDULED)
                .build();

        return showRepository.save(show);
    }

    @Transactional
    public Show updateShowStatus(Long id, Show.ShowStatus status) {
        Show show = getShowById(id);
        show.setStatus(status);
        return showRepository.save(show);
    }
}
