package com.example.roombooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    private String reservedBy;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public Reservation() {}

    public Reservation(Room room, String reservedBy, LocalDateTime startTime, LocalDateTime endTime) {
        this.room = room;
        this.reservedBy = reservedBy;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getReservedBy() { return reservedBy; }
    public void setReservedBy(String reservedBy) { this.reservedBy = reservedBy; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
