package com.example.roombooking.service;

import com.example.roombooking.model.Reservation;

import java.util.List;

public interface ReservationService {
    List<Reservation> findAll();
    Reservation findById(Long id);
    List<Reservation> findByRoomId(Long roomId);
    Reservation create(Reservation reservation);
    void delete(Long id);
}
