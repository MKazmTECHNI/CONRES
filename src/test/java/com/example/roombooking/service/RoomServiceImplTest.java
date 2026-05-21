package com.example.roombooking.service;

import com.example.roombooking.exception.ResourceNotFoundException;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.ReservationRepository;
import com.example.roombooking.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    void should_returnAllRooms() {
        when(roomRepository.findAll()).thenReturn(List.of(new Room("Sala A", 10, 1, true)));

        List<Room> rooms = roomService.findAll();

        assertEquals(1, rooms.size());
        assertEquals("Sala A", rooms.get(0).getName());
    }

    @Test
    void should_throwException_when_roomNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.findById(99L));
    }

    @Test
    void should_saveRoom() {
        Room room = new Room("Sala X", 30, 2, true);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room saved = roomService.save(room);

        assertEquals("Sala X", saved.getName());
        verify(roomRepository).save(room);
    }

    @Test
    void should_updateRoom() {
        Room existing = new Room("Sala A", 10, 1, true);
        existing.setId(1L);
        Room updated = new Room("Sala A Updated", 15, 2, false);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Room result = roomService.update(1L, updated);

        assertEquals("Sala A Updated", result.getName());
        assertEquals(15, result.getCapacity());
        assertEquals(2, result.getFloor());
        assertFalse(result.isAvailable());
    }

    @Test
    void should_deleteRoom() {
        Room room = new Room("Sala A", 10, 1, true);
        room.setId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        roomService.delete(1L);

        verify(roomRepository).delete(room);
    }

    @Test
    void should_throwException_when_deletingNonExistentRoom() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.delete(99L));
    }

    @Test
    void should_returnAvailableRooms() {
        Room room1 = new Room("Sala A", 10, 1, true);
        room1.setId(1L);
        Room room2 = new Room("Sala B", 5, 1, true);
        room2.setId(2L);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));
        when(reservationRepository.findByRoomIdAndStartTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of());

        List<Room> available = roomService.findAvailable(LocalDateTime.now(), 8);

        assertEquals(1, available.size());
        assertEquals("Sala A", available.get(0).getName());
    }
}
