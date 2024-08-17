package com.ridetogether.booking.service;

import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.model.Booking;
import java.util.List;

public interface BookingService {
  Booking initiateBooking(BookingDTO bookingDTO);

  String generateId();

  String successBooking(String bookingId);

  String deleteBooking(String bookingId);

  Booking getBooking(String bookingId);

  List<Booking> getBookings(String scheduleId);
}
