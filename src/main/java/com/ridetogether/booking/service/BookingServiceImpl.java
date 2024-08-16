package com.ridetogether.booking.service;

import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.model.Booking;
import com.ridetogether.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService{
    @Autowired
    BookingRepository bookingRepository;
    @Autowired HoldingService holdingService;
    @Override
    public Booking book(BookingDTO bookingDTO) {
        if(holdingService.checkSeatHoldStatus(bookingDTO.getBookedSeats())){
            holdingService.holdSeat(bookingDTO.getBookedSeats(),bookingDTO.getUserId());
        }
        Booking booking = Booking.builder().bookingDate(LocalDateTime.now())
                .build();
       return  bookingRepository.save(booking);
    }
}
