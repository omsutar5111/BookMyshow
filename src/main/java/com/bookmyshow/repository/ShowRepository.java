package com.bookmyshow.repository;

import com.bookmyshow.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieId(Long movieId);

    List<Show> findByMovieIdAndShowDate(Long movieId, LocalDate showDate);

    List<Show> findByShowDateAndStatus(LocalDate showDate, Show.ShowStatus status);

    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.showDate >= :date AND s.status = 'SCHEDULED' ORDER BY s.showDate, s.showTime")
    List<Show> findUpcomingShowsByMovie(@Param("movieId") Long movieId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Show s JOIN s.screen sc JOIN sc.theater t WHERE t.city = :city AND s.movie.id = :movieId AND s.showDate = :date AND s.status = 'SCHEDULED'")
    List<Show> findShowsByCityAndMovieAndDate(@Param("city") String city, @Param("movieId") Long movieId, @Param("date") LocalDate date);
}
