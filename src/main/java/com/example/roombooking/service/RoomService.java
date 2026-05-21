package com.example.roombooking.service;

import com.example.roombooking.model.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {
    List<Room> findAll();
    Room findById(Long id);
    Room save(Room room);
    Room update(Long id, Room room);
    void delete(Long id);
    List<Room> findAvailable(LocalDateTime start, int minCapacity);
}
