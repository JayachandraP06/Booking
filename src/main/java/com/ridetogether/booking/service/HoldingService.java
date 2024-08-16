package com.ridetogether.booking.service;

import com.ridetogether.booking.model.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class HoldingService {
    private static final long HOLD_DURATION = 5;
    @Autowired private StringRedisTemplate redisTemplate;

    public boolean checkSeatHoldStatus(List<Seat> seatList) {
        for(Seat seat: seatList){
            if(redisTemplate.hasKey(seat.getSeatId())){
                return false;
            }
        }
        return true;
    }

    public void holdSeat(List<Seat> seatList, String userId) {
        for(Seat seat: seatList) {
            redisTemplate.opsForValue().set(seat.getSeatId(), userId, HOLD_DURATION, TimeUnit.MINUTES);
        }
    }

    public boolean confirmBooking(String seatId, String userId) {
        String currentHolder = redisTemplate.opsForValue().get(seatId);
        if(userId.equals(currentHolder)) {
            redisTemplate.delete(seatId);
            return true;
        }
        return false;
    }

    public void releaseHold(String seatId) {
        redisTemplate.delete(seatId);
    }
}
