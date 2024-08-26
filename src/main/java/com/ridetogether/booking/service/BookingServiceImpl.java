package com.ridetogether.booking.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.razorpay.*;
import com.ridetogether.booking.config.RazorpayConfig;
import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.enums.BookingStatus;
import com.ridetogether.booking.model.Booking;
import com.ridetogether.booking.repository.BookingRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
  @Autowired BookingRepository bookingRepository;
  @Autowired HoldingService holdingService;

  @Autowired
  private RazorpayClient razorpayClient;

  @Autowired
  private RazorpayConfig razorpayConfig;

  @Override
  public Booking initiateBooking(BookingDTO bookingDTO) throws RazorpayException {

    JSONObject razorpayReq=new JSONObject();

    razorpayReq.put("amount",bookingDTO.getAmount().multiply(new BigDecimal(100)));
    razorpayReq.put("currency","INR");
    razorpayReq.put("reciept",bookingDTO.getUserEmail());


    Order order=razorpayClient.orders.create(razorpayReq);
    log.info("payment initiated successfully in razorpay ");
    Booking booking =
            Booking.builder()
                    .bookingId(generateId())
                    .bookingDate(LocalDateTime.now())
                    .source(bookingDTO.getSource())
                    .destination(bookingDTO.getDestination())
                    .bookedSeats(bookingDTO.getBookedSeats())
                    .amount(bookingDTO.getAmount())
                    .scheduleId(bookingDTO.getScheduleId())
                    .vehicleId(bookingDTO.getVehicleId())
                    .userId(bookingDTO.getUserId())
                    .travelDate(bookingDTO.getTravelDate())
                    .status(BookingStatus.INITIATED)
                    .paymentId(order.get("id"))
                    .build();
    return bookingRepository.save(booking);
  }

  public String successBooking(String paymentId) {
    Booking record = bookingRepository.findByPaymentId(paymentId).orElse(null);
    if (Objects.nonNull(record)) {
      record.setStatus(BookingStatus.BOOKED);
      bookingRepository.save(record);
      return "SuccessFully Booked";
    }
    return "Not Booked";
  }

  public String deleteBooking(String paymentId) {
    bookingRepository.deleteByPaymentId(paymentId);
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

  public boolean verifyPaymentSignature(String orderId, String paymentId, String razorpaySignature) throws RazorpayException {
    String payload = orderId + "|" + paymentId;
    String generatedSignature = Utils.getHash(payload,razorpayConfig.getRazorpaySecret() );
    return generatedSignature.equals(razorpaySignature);
  }

  public Payment fetchPaymentDetails(String paymentId) throws RazorpayException {
    return razorpayClient.payments.fetch(paymentId);
  }
}
