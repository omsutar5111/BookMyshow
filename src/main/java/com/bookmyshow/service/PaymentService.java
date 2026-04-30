package com.bookmyshow.service;

import com.bookmyshow.exception.ResourceNotFoundException;
import com.bookmyshow.model.Booking;
import com.bookmyshow.model.Payment;
import com.bookmyshow.repository.BookingRepository;
import com.bookmyshow.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private static final double SIMULATED_PAYMENT_FAILURE_RATE = 0.05;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Processes payment for a booking.
     * In a real application, this would integrate with a payment gateway (e.g., Razorpay, Stripe, PayU).
     * Here we simulate the payment process.
     */
    @Transactional
    public Payment processPayment(Booking booking, Payment.PaymentMethod paymentMethod) {
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .paymentMethod(paymentMethod)
                .status(Payment.PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);

        // Simulate payment processing
        boolean paymentSuccessful = simulatePaymentGateway(booking.getTotalAmount(), paymentMethod);

        if (paymentSuccessful) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setTransactionId(generateTransactionId());
            payment.setPaymentTime(LocalDateTime.now());

            booking.setStatus(Booking.BookingStatus.CONFIRMED);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Payment declined by gateway");
            booking.setStatus(Booking.BookingStatus.FAILED);
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return payment;
    }

    @Transactional
    public Payment refundPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));

        if (payment.getStatus() != Payment.PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException("Only successful payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));
    }

    private boolean simulatePaymentGateway(java.math.BigDecimal amount, Payment.PaymentMethod method) {
        // Simulate 95% success rate for all payment methods
        return Math.random() > SIMULATED_PAYMENT_FAILURE_RATE;
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
