package com.ridetogether.booking.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.ridetogether.booking.dto.BookingDTO;
import com.ridetogether.booking.model.Booking;
import com.ridetogether.booking.service.BookingService;
import com.ridetogether.booking.service.HoldingService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.razorpay.Utils.verifyPaymentSignature;

@CrossOrigin
@RestController
@RequestMapping("bookings")
public class BookingController {

  @Autowired BookingService bookingService;
  @Autowired HoldingService holdingService;
  @Autowired
  RazorpayClient razorpayClient;

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
  public ResponseEntity<?> book(@RequestBody BookingDTO bookingDTO) throws RazorpayException {
    Booking booking = bookingService.initiateBooking(bookingDTO);
    return new ResponseEntity<>(booking, HttpStatus.CREATED);
  }


  @DeleteMapping("/{bookingId}")
  public ResponseEntity<?> deleteBooking(@PathVariable String bookingId) {
    return new ResponseEntity<>(bookingService.deleteBooking(bookingId), HttpStatus.OK);
  }

  @PutMapping("/update/{paymentId}")
  public ResponseEntity<?> updateBooking(@PathVariable String paymentId) throws RazorpayException {

     String status=razorpayClient.payments.fetch(paymentId).get("status");
     if(status.equalsIgnoreCase("Paid")) {
       return new ResponseEntity<>(bookingService.successBooking(paymentId), HttpStatus.OK);
     }
     else{
       if(status.equalsIgnoreCase("Failed")){
         return new ResponseEntity<>(bookingService.deleteBooking(paymentId), HttpStatus.OK);
       }
     }
     return new ResponseEntity<>(status,HttpStatus.OK);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getBooking(@PathVariable String bookingId) {

    return new ResponseEntity<>(bookingService.getBooking(bookingId), HttpStatus.OK);
  }

  @GetMapping("/schedule/{scheduleId}")
  public ResponseEntity<?> getBookings(@PathVariable String scheduleId) {
    return new ResponseEntity<>(bookingService.getBookings(scheduleId), HttpStatus.OK);
  }

  @PostMapping("/verify")
  public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, Object> paymentData) {
    Map<String, Object> response = new HashMap<>();
    String razorpayPaymentId = (String) paymentData.get("razorpay_payment_id");
    String razorpayOrderId = (String) paymentData.get("razorpay_order_id");
    String razorpaySignature = (String) paymentData.get("razorpay_signature");

    try {
      // Verify payment signature
      boolean isSignatureValid = bookingService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

      if (isSignatureValid) {
        // Check payment status by fetching payment details from Razorpay
        Payment payment = bookingService.fetchPaymentDetails(razorpayPaymentId);
        if (payment != null && "captured".equals(payment.get("status"))) {

          bookingService.successBooking(razorpayOrderId);
          response.put("success", true);
          response.put("message", "Payment verified and successful.");
        } else {
          bookingService.deleteBooking(razorpayOrderId);
          response.put("success", false);
          response.put("message", "Payment verification failed or payment was not successful.");
        }
      } else {
        response.put("success", false);
        response.put("message", "Payment signature verification failed.");
      }
    } catch (RazorpayException e) {
      response.put("success", false);
      response.put("message", "Payment verification failed with exception.");
    }

    return ResponseEntity.ok(response);
  }

  @PostMapping("/webhook")
  public ResponseEntity<String> handleWebhook(@RequestBody JsonNode webhookData) {
    String event = String.valueOf(webhookData.get("event"));
    JsonNode payload = webhookData.get("payload");
    JsonNode paymentEntity = payload.get("payment").get("entity");
    String paymentId = String.valueOf(paymentEntity.get("id"));
    String orderId = String.valueOf(paymentEntity.get("order_id"));
    if ("payment.captured".equals(event)) {
      // Handle payment success
      // Update your order status in the database as successful
      bookingService.successBooking(orderId);
      System.out.println("Payment successful: " + paymentId);
    } else if ("payment.failed".equals(event)) {
      // Handle payment failure
      // Update your order status in the database as failed
      bookingService.deleteBooking(orderId);
      System.out.println("Payment failed: " + paymentId);
    }

    return ResponseEntity.ok("Webhook handled");
  }

}

