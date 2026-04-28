package com.bookmyshow.service;

import com.bookmyshow.exception.ResourceNotFoundException;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Theater;
import com.bookmyshow.repository.ScreenRepository;
import com.bookmyshow.repository.SeatRepository;
import com.bookmyshow.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TheaterService {

    private static final double PREMIUM_ROW_START_RATIO = 0.4;
    private static final double VIP_ROW_START_RATIO = 0.75;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public List<Theater> getTheatersByCity(String city) {
        return theaterRepository.findByCityIgnoreCase(city);
    }

    public Theater getTheaterById(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + id));
    }

    @Transactional
    public Theater createTheater(Theater theater) {
        return theaterRepository.save(theater);
    }

    @Transactional
    public Theater updateTheater(Long id, Theater theaterData) {
        Theater theater = getTheaterById(id);
        theater.setName(theaterData.getName());
        theater.setAddress(theaterData.getAddress());
        theater.setCity(theaterData.getCity());
        theater.setState(theaterData.getState());
        theater.setPincode(theaterData.getPincode());
        theater.setContactNumber(theaterData.getContactNumber());
        return theaterRepository.save(theater);
    }

    @Transactional
    public Screen addScreen(Long theaterId, String screenName, int rows, int seatsPerRow) {
        Theater theater = getTheaterById(theaterId);
        int totalSeats = rows * seatsPerRow;

        Screen screen = Screen.builder()
                .name(screenName)
                .theater(theater)
                .totalSeats(totalSeats)
                .build();
        screen = screenRepository.save(screen);

        List<Seat> seats = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            char rowLabel = (char) ('A' + row);
            Seat.SeatType seatType = determineSeatType(row, rows);
            for (int col = 1; col <= seatsPerRow; col++) {
                Seat seat = Seat.builder()
                        .seatNumber(String.valueOf(col))
                        .rowLabel(String.valueOf(rowLabel))
                        .seatType(seatType)
                        .screen(screen)
                        .build();
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
        return screen;
    }

    private Seat.SeatType determineSeatType(int rowIndex, int totalRows) {
        double ratio = (double) rowIndex / totalRows;
        if (ratio < PREMIUM_ROW_START_RATIO) {
            return Seat.SeatType.NORMAL;
        } else if (ratio < VIP_ROW_START_RATIO) {
            return Seat.SeatType.PREMIUM;
        } else {
            return Seat.SeatType.VIP;
        }
    }

    public List<Screen> getScreensByTheater(Long theaterId) {
        return screenRepository.findByTheaterId(theaterId);
    }

    public List<Seat> getSeatsByScreen(Long screenId) {
        return seatRepository.findByScreenId(screenId);
    }
}
