package com.bookmyshow.controller;

import com.bookmyshow.dto.ShowRequest;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Show;
import com.bookmyshow.repository.SeatRepository;
import com.bookmyshow.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @Autowired
    private SeatRepository seatRepository;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Show>> getShowsByMovie(
            @PathVariable Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date != null) {
            return ResponseEntity.ok(showService.getShowsByMovieAndDate(movieId, date));
        }
        return ResponseEntity.ok(showService.getUpcomingShowsByMovie(movieId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @GetMapping("/{id}/available-seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        Long screenId = show.getScreen().getId();
        List<Seat> availableSeats = seatRepository.findAvailableSeatsByShowAndScreen(id, screenId);
        return ResponseEntity.ok(availableSeats);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Show>> searchShows(
            @RequestParam Long movieId,
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(showService.getShowsByCityAndMovieAndDate(city, movieId, date));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Show> createShow(@Valid @RequestBody ShowRequest request) {
        return ResponseEntity.ok(showService.createShow(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Show> updateShowStatus(
            @PathVariable Long id,
            @RequestParam Show.ShowStatus status) {
        return ResponseEntity.ok(showService.updateShowStatus(id, status));
    }
}
