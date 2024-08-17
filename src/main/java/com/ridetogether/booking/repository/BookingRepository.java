package com.ridetogether.booking.repository;

import com.ridetogether.booking.model.Booking;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
  public List<Booking> findByScheduleId(String scheduleId);
}
