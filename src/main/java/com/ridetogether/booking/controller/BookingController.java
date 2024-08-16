package com.ridetogether.booking.controller;

import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.service.HoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bookings")
public class BookingController {

    @Autowired
    HoldingService holdingService;

    @PostMapping("/continue")
    public ResponseEntity<?> hold(@RequestBody BookingDTO bookingDTO)
    {
        holdingService.holdSeat(bookingDTO.getBookedSeats(), bookingDTO.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
