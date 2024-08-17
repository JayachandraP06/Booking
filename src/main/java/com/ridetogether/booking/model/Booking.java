package com.ridetogether.booking.model;

import com.ridetogether.booking.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Booking")
public class Booking {
  @Id private String bookingId;
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
