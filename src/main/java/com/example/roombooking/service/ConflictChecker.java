package com.example.roombooking.service;

import com.example.roombooking.repository.ReservationRepository;

import java.time.LocalDateTime;

/**
 * ConflictChecker with prototype scope — a new instance is created per request.
 * This ensures each reservation check operates independently, avoiding shared state
 * issues and making the checker thread-safe for concurrent booking attempts.
 */
public class ConflictChecker {

    private final ReservationRepository reservationRepository;

    public ConflictChecker(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean hasConflict(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return !reservationRepository
                .findByRoomIdAndStartTimeBetween(roomId, startTime, endTime)
                .isEmpty();
    }
}
