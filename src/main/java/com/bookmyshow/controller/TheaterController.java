package com.bookmyshow.controller;

import com.bookmyshow.dto.ApiResponse;
import com.bookmyshow.dto.ScreenRequest;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Theater;
import com.bookmyshow.service.TheaterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    @Autowired
    private TheaterService theaterService;

    @GetMapping
    public ResponseEntity<List<Theater>> getAllTheaters(@RequestParam(required = false) String city) {
        if (city != null && !city.isBlank()) {
            return ResponseEntity.ok(theaterService.getTheatersByCity(city));
        }
        return ResponseEntity.ok(theaterService.getAllTheaters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theater> getTheaterById(@PathVariable Long id) {
        return ResponseEntity.ok(theaterService.getTheaterById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Theater> createTheater(@RequestBody Theater theater) {
        return ResponseEntity.ok(theaterService.createTheater(theater));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Theater> updateTheater(@PathVariable Long id, @RequestBody Theater theater) {
        return ResponseEntity.ok(theaterService.updateTheater(id, theater));
    }

    @PostMapping("/{id}/screens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Screen> addScreen(
            @PathVariable Long id,
            @Valid @RequestBody ScreenRequest request) {
        return ResponseEntity.ok(theaterService.addScreen(id, request.getName(), request.getRows(), request.getSeatsPerRow()));
    }

    @GetMapping("/{id}/screens")
    public ResponseEntity<List<Screen>> getScreensByTheater(@PathVariable Long id) {
        return ResponseEntity.ok(theaterService.getScreensByTheater(id));
    }

    @GetMapping("/screens/{screenId}/seats")
    public ResponseEntity<List<Seat>> getSeatsByScreen(@PathVariable Long screenId) {
        return ResponseEntity.ok(theaterService.getSeatsByScreen(screenId));
    }
}
