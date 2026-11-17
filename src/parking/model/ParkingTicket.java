package parking.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import parking.interfaces.ParkingPass;

public class ParkingTicket {
    private static final AtomicLong NEXT_NUMBER = new AtomicLong(1);

    private final String ticketNumber;
    private final Vehicle vehicle;
    private final ParkingSlot slot;
    private final LocalDateTime entryTime;
    private final ParkingPass pass;
    private LocalDateTime exitTime;

    public ParkingTicket(Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime,
                         ParkingPass pass) {
        if (vehicle == null || slot == null || entryTime == null) {
            throw new IllegalArgumentException("Vehicle, slot and entry time are required.");
        }
        this.ticketNumber = String.format("T%06d", NEXT_NUMBER.getAndIncrement());
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = entryTime;
        this.pass = pass;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public ParkingPass getPass() {
        return pass;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void closeAt(LocalDateTime exitTime) {
        if (exitTime == null || exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time must not be before entry time.");
        }
        if (this.exitTime != null) {
            throw new IllegalStateException("Ticket is already closed.");
        }
        this.exitTime = exitTime;
    }

    @Override
    public String toString() {
        return ticketNumber + " " + vehicle + " floor " + slot.getFloorNumber()
                + ", slot " + slot.getSlotNumber();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ParkingTicket)) {
            return false;
        }
        ParkingTicket ticket = (ParkingTicket) other;
        return ticketNumber.equals(ticket.ticketNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketNumber);
    }
}
