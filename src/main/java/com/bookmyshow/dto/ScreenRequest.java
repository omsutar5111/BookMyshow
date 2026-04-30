package com.bookmyshow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScreenRequest {

    @NotBlank(message = "Screen name is required")
    private String name;

    @NotNull(message = "Number of rows is required")
    @Min(value = 1, message = "Rows must be at least 1")
    private Integer rows;

    @NotNull(message = "Seats per row is required")
    @Min(value = 1, message = "Seats per row must be at least 1")
    private Integer seatsPerRow;
}
