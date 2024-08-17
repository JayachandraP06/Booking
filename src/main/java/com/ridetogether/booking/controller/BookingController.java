package com.ridetogether.booking.controller;

import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.model.Booking;
import com.ridetogether.booking.service.BookingService;
import com.ridetogether.booking.service.HoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("bookings")
public class BookingController {

  @Autowired BookingService bookingService;
  @Autowired HoldingService holdingService;

  @PostMapping("/continue")
  public ResponseEntity<?> hold(@RequestBody BookingDTO bookingDTO) {
    if (holdingService.checkSeatHoldStatus(
        bookingDTO.getBookedSeats(), bookingDTO.getScheduleId())) {
      holdingService.holdSeat(
          bookingDTO.getBookedSeats(), bookingDTO.getUserId(), bookingDTO.getScheduleId());
      return new ResponseEntity<>("Seats held successfully...", HttpStatus.OK);
    }
    return new ResponseEntity<>("Seats are already held!!!", HttpStatus.BAD_REQUEST);
  }

  @PostMapping
  public ResponseEntity<?> book(@RequestBody BookingDTO bookingDTO) {
    Booking booking = bookingService.initiateBooking(bookingDTO);
    return new ResponseEntity<>(booking, HttpStatus.CREATED);
  }

  @DeleteMapping("/{bookingId}")
  public ResponseEntity<?> deleteBooking(@PathVariable String bookingId) {
    return new ResponseEntity<>(bookingService.deleteBooking(bookingId), HttpStatus.OK);
  }

  @PutMapping("/{bookingId}")
  public ResponseEntity<?> updateBooking(@PathVariable String bookingId) {
    return new ResponseEntity<>(bookingService.successBooking(bookingId), HttpStatus.OK);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getBooking(@PathVariable String bookingId) {
    return new ResponseEntity<>(bookingService.getBooking(bookingId), HttpStatus.OK);
  }

  @GetMapping("/schedule/{scheduleId}")
  public ResponseEntity<?> getBookings(@PathVariable String scheduleId) {
    return new ResponseEntity<>(bookingService.getBookings(scheduleId), HttpStatus.OK);
  }
}
