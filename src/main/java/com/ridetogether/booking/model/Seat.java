package com.ridetogether.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    private String seatId;
    private Passenger passenger;
    private SeatStatus status;

}
