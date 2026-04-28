package com.bookmyshow.controller;

import com.bookmyshow.dto.ApiResponse;
import com.bookmyshow.dto.BookingRequest;
import com.bookmyshow.model.Booking;
import com.bookmyshow.model.Payment;
import com.bookmyshow.service.BookingService;
import com.bookmyshow.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Booking booking = bookingService.createBooking(userDetails.getUsername(), request);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getUserBookings(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getBookingById(id, userDetails.getUsername()));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable String reference) {
        return ResponseEntity.ok(bookingService.getBookingByReference(reference));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        bookingService.cancelBooking(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Booking cancelled and refund initiated"));
    }

    @GetMapping("/{id}/payment")
    public ResponseEntity<Payment> getPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Verify the booking belongs to the user (via getBookingById check)
        bookingService.getBookingById(id, userDetails.getUsername());
        return ResponseEntity.ok(paymentService.getPaymentByBookingId(id));
    }
}
