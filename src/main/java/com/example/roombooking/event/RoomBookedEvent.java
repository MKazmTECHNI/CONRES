package com.example.roombooking.event;

import com.example.roombooking.model.Reservation;
import org.springframework.context.ApplicationEvent;

public class RoomBookedEvent extends ApplicationEvent {

    private final Reservation reservation;

    public RoomBookedEvent(Reservation reservation) {
        super(reservation);
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
