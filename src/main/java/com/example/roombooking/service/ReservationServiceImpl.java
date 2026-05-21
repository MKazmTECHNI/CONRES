package com.example.roombooking.service;

import com.example.roombooking.event.RoomBookedEvent;
import com.example.roombooking.exception.ConflictException;
import com.example.roombooking.exception.ResourceNotFoundException;
import com.example.roombooking.model.Reservation;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.ReservationRepository;
import com.example.roombooking.repository.RoomRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ConflictChecker conflictChecker;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RoomRepository roomRepository,
                                  ApplicationEventPublisher eventPublisher,
                                  ConflictChecker conflictChecker) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.eventPublisher = eventPublisher;
        this.conflictChecker = conflictChecker;
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    @Override
    public List<Reservation> findByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId);
    }

    @Override
    public Reservation create(Reservation reservation) {
        Room room = roomRepository.findById(reservation.getRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + reservation.getRoom().getId()));

        if (conflictChecker.hasConflict(room.getId(), reservation.getStartTime(), reservation.getEndTime())) {
            throw new ConflictException("Room is already booked for the selected time period");
        }

        reservation.setRoom(room);
        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.publishEvent(new RoomBookedEvent(saved));
        return saved;
    }

    @Override
    public void delete(Long id) {
        Reservation reservation = findById(id);
        reservationRepository.delete(reservation);
    }
}
