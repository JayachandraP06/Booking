package com.ridetogether.booking.service;

import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.model.Booking;

public interface BookingService {
    Booking book(BookingDTO bookingDTO);
}
