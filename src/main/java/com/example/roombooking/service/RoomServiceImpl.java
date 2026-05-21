package com.example.roombooking.service;

import com.example.roombooking.exception.ResourceNotFoundException;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.ReservationRepository;
import com.example.roombooking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomServiceImpl(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public Room update(Long id, Room room) {
        Room existing = findById(id);
        existing.setName(room.getName());
        existing.setCapacity(room.getCapacity());
        existing.setFloor(room.getFloor());
        existing.setAvailable(room.isAvailable());
        return roomRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Room room = findById(id);
        roomRepository.delete(room);
    }

    @Override
    public List<Room> findAvailable(LocalDateTime start, int minCapacity) {
        LocalDateTime end = start.plusHours(1);
        List<Room> allRooms = roomRepository.findAll();
        return allRooms.stream()
                .filter(Room::isAvailable)
                .filter(r -> r.getCapacity() >= minCapacity)
                .filter(r -> reservationRepository
                        .findByRoomIdAndStartTimeBetween(r.getId(), start, end)
                        .isEmpty())
                .toList();
    }
}
