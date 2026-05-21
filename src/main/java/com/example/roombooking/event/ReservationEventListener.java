package com.example.roombooking.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationEventListener {

    @EventListener
    public void handleRoomBooked(RoomBookedEvent event) {
        var reservation = event.getReservation();
        System.out.println("New reservation created: Room '" + reservation.getRoom().getName()
                + "' booked by " + reservation.getReservedBy()
                + " from " + reservation.getStartTime()
                + " to " + reservation.getEndTime());
    }
}
