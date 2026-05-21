package com.example.roombooking.repository;

import com.example.roombooking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRoomIdAndStartTimeBetween(Long roomId, LocalDateTime from, LocalDateTime to);
    List<Reservation> findByRoomId(Long roomId);
    List<Reservation> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);
}
