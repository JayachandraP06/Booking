package com.ridetogether.booking.service;

import com.razorpay.Payment;
import com.razorpay.RazorpayException;
import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.model.Booking;
import java.util.List;

public interface BookingService {
  Booking initiateBooking(BookingDTO bookingDTO) throws RazorpayException;

  String generateId();

  String successBooking(String paymentId);

  String deleteBooking(String paymentId);

  Booking getBooking(String bookingId);

  List<Booking> getBookings(String scheduleId);

   boolean verifyPaymentSignature(String orderId, String paymentId, String razorpaySignature) throws RazorpayException;
   Payment fetchPaymentDetails(String paymentId) throws RazorpayException;


  }
