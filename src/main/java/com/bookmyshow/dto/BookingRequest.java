package com.bookmyshow.dto;

import com.bookmyshow.model.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotNull(message = "Seat IDs are required")
    private List<Long> seatIds;

    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;
}
