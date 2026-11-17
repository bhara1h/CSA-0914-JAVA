package parking.model;

public class ParkingSlot {
    private final int floorNumber;
    private final int slotNumber;
    private Vehicle vehicle;
    private ParkingTicket ticket;

    public ParkingSlot(int floorNumber, int slotNumber) {
        if (floorNumber < 0 || slotNumber < 0) {
            throw new IllegalArgumentException("Floor and slot numbers cannot be negative.");
        }
        this.floorNumber = floorNumber;
        this.slotNumber = slotNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingTicket getTicket() {
        return ticket;
    }

    public boolean isAvailable() {
        return vehicle == null;
    }

    public void park(Vehicle vehicle, ParkingTicket ticket) {
        if (!isAvailable()) {
            throw new IllegalStateException("Parking slot is occupied.");
        }
        if (vehicle == null || ticket == null) {
            throw new IllegalArgumentException("Vehicle and ticket are required.");
        }
        if (ticket.getSlot() != this || ticket.getVehicle() != vehicle) {
            throw new IllegalArgumentException("Ticket must refer to this vehicle and slot.");
        }
        this.vehicle = vehicle;
        this.ticket = ticket;
    }

    public void release() {
        if (isAvailable()) {
            throw new IllegalStateException("Parking slot is already available.");
        }
        vehicle = null;
        ticket = null;
    }
}
