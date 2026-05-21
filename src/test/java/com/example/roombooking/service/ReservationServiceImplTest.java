package com.example.roombooking.service;

import com.example.roombooking.event.RoomBookedEvent;
import com.example.roombooking.exception.ConflictException;
import com.example.roombooking.exception.ResourceNotFoundException;
import com.example.roombooking.model.Reservation;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.ReservationRepository;
import com.example.roombooking.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ConflictChecker conflictChecker;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void should_returnAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation()));

        List<Reservation> reservations = reservationService.findAll();

        assertEquals(1, reservations.size());
    }

    @Test
    void should_throwException_when_reservationNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.findById(99L));
    }

    @Test
    void should_createReservation() {
        Room room = new Room("Sala A", 10, 1, true);
        room.setId(1L);
        Reservation reservation = new Reservation(room, "Jan Kowalski",
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 1, 11, 0));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(conflictChecker.hasConflict(anyLong(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation created = reservationService.create(reservation);

        assertNotNull(created);
        assertEquals("Jan Kowalski", created.getReservedBy());
        verify(eventPublisher).publishEvent(any(RoomBookedEvent.class));
    }

    @Test
    void should_throwException_when_creatingReservationWithConflict() {
        Room room = new Room("Sala A", 10, 1, true);
        room.setId(1L);
        Reservation reservation = new Reservation(room, "Jan Kowalski",
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 1, 11, 0));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(conflictChecker.hasConflict(anyLong(), any(), any())).thenReturn(true);

        assertThrows(ConflictException.class, () -> reservationService.create(reservation));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void should_deleteReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.delete(1L);

        verify(reservationRepository).delete(reservation);
    }
}
