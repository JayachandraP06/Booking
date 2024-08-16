package com.ridetogether.booking.dto;

import com.ridetogether.booking.enums.BookingStatus;
import com.ridetogether.booking.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String bookingId;
    private String source;
    private String destination;
    private String scheduleId;
    private String vehicleId;
    private LocalDateTime travelDate;
    private LocalDateTime bookingDate;
    private String userId;
    private List<Seat> bookedSeats;
    private BigDecimal amount;
    private BookingStatus status;
}
