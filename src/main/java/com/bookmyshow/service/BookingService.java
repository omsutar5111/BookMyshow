package com.bookmyshow.service;

import com.bookmyshow.dto.BookingRequest;
import com.bookmyshow.exception.BookingException;
import com.bookmyshow.exception.ResourceNotFoundException;
import com.bookmyshow.model.*;
import com.bookmyshow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @Transactional
    public Booking createBooking(String userEmail, BookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + request.getShowId()));

        if (show.getStatus() != Show.ShowStatus.SCHEDULED) {
            throw new BookingException("Show is not available for booking");
        }

        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new BookingException("At least one seat must be selected");
        }

        // Validate and fetch seats
        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());
        if (seats.size() != request.getSeatIds().size()) {
            throw new BookingException("One or more selected seats are invalid");
        }

        // Verify seats belong to the show's screen
        Long screenId = show.getScreen().getId();
        boolean allSeatsValid = seats.stream()
                .allMatch(seat -> seat.getScreen().getId().equals(screenId));
        if (!allSeatsValid) {
            throw new BookingException("Selected seats do not belong to the show's screen");
        }

        // Check seat availability
        List<Seat> availableSeats = seatRepository.findAvailableSeatsByShowAndScreen(show.getId(), screenId);
        List<Long> availableSeatIds = availableSeats.stream().map(Seat::getId).toList();

        boolean allAvailable = request.getSeatIds().stream().allMatch(availableSeatIds::contains);
        if (!allAvailable) {
            throw new BookingException("One or more selected seats are already booked");
        }

        if (show.getAvailableSeats() < request.getSeatIds().size()) {
            throw new BookingException("Not enough available seats");
        }

        // Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(seats, show);

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .seats(seats)
                .totalAmount(totalAmount)
                .status(Booking.BookingStatus.PENDING)
                .bookingReference(generateBookingReference())
                .build();

        booking = bookingRepository.save(booking);

        // Update available seats
        show.setAvailableSeats(show.getAvailableSeats() - seats.size());
        showRepository.save(show);

        // Process payment
        Payment payment = paymentService.processPayment(booking, request.getPaymentMethod());

        // If payment failed, restore seat availability
        if (payment.getStatus() == Payment.PaymentStatus.FAILED) {
            show.setAvailableSeats(show.getAvailableSeats() + seats.size());
            showRepository.save(show);
        }

        return booking;
    }

    public List<Booking> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserId(user.getId());
    }

    public Booking getBookingById(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Users can only view their own bookings; admins can view all
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != User.Role.ADMIN && !booking.getUser().getId().equals(user.getId())) {
            throw new BookingException("You are not authorized to view this booking");
        }

        return booking;
    }

    @Transactional
    public Booking cancelBooking(Long id, String userEmail) {
        Booking booking = getBookingById(id, userEmail);

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new BookingException("Only confirmed bookings can be cancelled");
        }

        // Restore seat availability
        Show show = booking.getShow();
        show.setAvailableSeats(show.getAvailableSeats() + booking.getSeats().size());
        showRepository.save(show);

        // Initiate refund
        paymentService.refundPayment(booking.getId());

        return booking;
    }

    public Booking getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + reference));
    }

    private BigDecimal calculateTotalAmount(List<Seat> seats, Show show) {
        BigDecimal total = BigDecimal.ZERO;
        for (Seat seat : seats) {
            BigDecimal price;
            switch (seat.getSeatType()) {
                case VIP -> price = show.getVipSeatPrice();
                case PREMIUM -> price = show.getPremiumSeatPrice();
                default -> price = show.getNormalSeatPrice();
            }
            total = total.add(price);
        }
        return total;
    }

    private String generateBookingReference() {
        return "BMS" + UUID.randomUUID().toString().replace("-", "").substring(0, 9).toUpperCase();
    }
}
