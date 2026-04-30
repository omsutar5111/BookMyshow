package com.bookmyshow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie_shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    private LocalDate showDate;

    private LocalTime showTime;

    @Column(precision = 10, scale = 2)
    private BigDecimal normalSeatPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal premiumSeatPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal vipSeatPrice;

    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShowStatus status = ShowStatus.SCHEDULED;

    @JsonIgnore
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    public enum ShowStatus {
        SCHEDULED, CANCELLED, COMPLETED
    }
}
