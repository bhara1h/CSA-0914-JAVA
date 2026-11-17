package parking.service;

import java.time.LocalDateTime;
import parking.interfaces.ParkingPass;
import parking.model.ParkingSlot;
import parking.model.ParkingTicket;
import parking.model.Vehicle;

public class ParkingFacility {
    private final ParkingSlot[][] floors;

    public ParkingFacility() {
        this(new int[] {4, 3, 5, 2, 4});
    }

    public ParkingFacility(int[] slotCountsByFloor) {
        if (slotCountsByFloor == null || slotCountsByFloor.length == 0) {
            throw new IllegalArgumentException("At least one floor is required.");
        }
        floors = new ParkingSlot[slotCountsByFloor.length][];
        for (int floor = 0; floor < slotCountsByFloor.length; floor++) {
            if (slotCountsByFloor[floor] < 0) {
                throw new IllegalArgumentException("Slot counts cannot be negative.");
            }
            floors[floor] = new ParkingSlot[slotCountsByFloor[floor]];
            for (int slot = 0; slot < slotCountsByFloor[floor]; slot++) {
                floors[floor][slot] = new ParkingSlot(floor, slot);
            }
        }
    }

    public ParkingTicket parkVehicle(Vehicle vehicle, LocalDateTime entryTime, ParkingPass pass) {
        if (vehicle == null || entryTime == null) {
            throw new IllegalArgumentException("Vehicle and entry time are required.");
        }
        for (ParkingSlot[] floor : floors) {
            for (ParkingSlot slot : floor) {
                if (slot.isAvailable()) {
                    ParkingTicket ticket = new ParkingTicket(vehicle, slot, entryTime, pass);
                    slot.park(vehicle, ticket);
                    return ticket;
                }
            }
        }
        return null;
    }

    public void releaseSlot(int floorNumber, int slotNumber) {
        getSlot(floorNumber, slotNumber).release();
    }

    public ParkingSlot getSlot(int floorNumber, int slotNumber) {
        if (floorNumber < 0 || floorNumber >= floors.length
                || slotNumber < 0 || slotNumber >= floors[floorNumber].length) {
            throw new IllegalArgumentException("Invalid floor or slot number.");
        }
        return floors[floorNumber][slotNumber];
    }

    public int getFloorCount() {
        return floors.length;
    }

    public int getTotalSlotCount() {
        int total = 0;
        for (ParkingSlot[] floor : floors) {
            total += floor.length;
        }
        return total;
    }

    public int getAvailableSlotCount() {
        int available = 0;
        for (ParkingSlot[] floor : floors) {
            for (ParkingSlot slot : floor) {
                if (slot.isAvailable()) {
                    available++;
                }
            }
        }
        return available;
    }
}
