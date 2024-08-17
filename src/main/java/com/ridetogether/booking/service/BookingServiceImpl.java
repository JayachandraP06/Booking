package com.ridetogether.booking.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.enums.BookingStatus;
import com.ridetogether.booking.model.Booking;
import com.ridetogether.booking.repository.BookingRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {
  @Autowired BookingRepository bookingRepository;
  @Autowired HoldingService holdingService;

  @Override
  public Booking initiateBooking(BookingDTO bookingDTO) {
    Booking booking =
        Booking.builder()
            .bookingId(generateId())
            .bookingDate(LocalDateTime.now())
            .source(bookingDTO.getSource())
            .destination(bookingDTO.getDestination())
            .bookedSeats(bookingDTO.getBookedSeats())
            .status(BookingStatus.PENDING)
            .amount(bookingDTO.getAmount())
            .scheduleId(bookingDTO.getScheduleId())
            .vehicleId(bookingDTO.getVehicleId())
            .userId(bookingDTO.getUserId())
            .travelDate(bookingDTO.getTravelDate())
            .build();
    return bookingRepository.save(booking);
  }

  public String successBooking(String bookingId) {
    Booking record = bookingRepository.findById(bookingId).orElse(null);
    if (Objects.nonNull(record)) {
      record.setStatus(BookingStatus.BOOKED);
      bookingRepository.save(record);
      return "SuccessFully Booked";
    }
    return "Not Booked";
  }

  public String deleteBooking(String bookingId) {
    bookingRepository.deleteById(bookingId);
    return "booking failed";
  }

  @Override
  public Booking getBooking(String bookingId) {
    Booking booking = bookingRepository.findById(bookingId).orElse(null);
    if (Objects.nonNull(booking)) {
      return booking;
    }
    return null;
  }

  @Override
  public List<Booking> getBookings(String scheduleId) {
    List<Booking> bookingList = bookingRepository.findByScheduleId(scheduleId);
    if (bookingList.isEmpty()) return null;
    return bookingList;
  }

  @Override
  public String generateId() {
    char[] numbers = "0123456789".toCharArray();
    SecureRandom random = new SecureRandom();

    ZoneId zoneId = ZoneId.of("Asia/Kolkata");
    ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
    LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
    return NanoIdUtils.randomNanoId(random, numbers, 4) + localDateTime.format(formatter);
  }
}
