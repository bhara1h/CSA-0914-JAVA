package parking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import parking.interfaces.ParkingPass;
import parking.model.ParkingTicket;
import parking.model.Vehicle;
import parking.pricing.PricingRule;

public class ParkingService {
    private final ParkingFacility facility;
    private final PricingRule pricingRule;
    private final Map<String, ParkingTicket> activeTickets = new HashMap<String, ParkingTicket>();
    private final Map<LocalDate, Double> dailyCollections = new HashMap<LocalDate, Double>();

    public ParkingService(ParkingFacility facility, PricingRule pricingRule) {
        if (facility == null || pricingRule == null) {
            throw new IllegalArgumentException("Facility and pricing rule are required.");
        }
        this.facility = facility;
        this.pricingRule = pricingRule;
    }

    public ParkingTicket enter(Vehicle vehicle) {
        return enter(vehicle, LocalDateTime.now(), null);
    }

    public ParkingTicket enter(Vehicle vehicle, LocalDateTime entryTime, ParkingPass pass) {
        if (vehicle == null || entryTime == null) {
            throw new IllegalArgumentException("Vehicle and entry time are required.");
        }
        String registration = vehicle.getRegistrationNumber();
        if (activeTickets.containsKey(registration)) {
            throw new IllegalStateException("Vehicle is already parked: " + registration);
        }
        if (pass != null && !pass.isValid(entryTime.toLocalDate())) {
            throw new IllegalArgumentException("Parking pass is not valid on the entry date.");
        }

        ParkingTicket ticket = facility.parkVehicle(vehicle, entryTime, pass);
        if (ticket == null) {
            throw new IllegalStateException("No parking slot is available.");
        }
        activeTickets.put(registration, ticket);
        return ticket;
    }

    public double exit(String ticketNumber, LocalDateTime exitTime) {
        if (ticketNumber == null || exitTime == null) {
            throw new IllegalArgumentException("Ticket number and exit time are required.");
        }
        ParkingTicket ticket = findActiveTicket(ticketNumber);
        if (ticket == null) {
            throw new IllegalArgumentException("Active ticket was not found.");
        }
        ticket.closeAt(exitTime);
        double fee = pricingRule.calculateFee(ticket.getVehicle(), ticket.getEntryTime(), exitTime);
        ParkingPass pass = ticket.getPass();
        if (pass != null && pass.isValid(exitTime.toLocalDate())) {
            fee = pass.applyTo(fee);
        }
        facility.releaseSlot(ticket.getSlot().getFloorNumber(), ticket.getSlot().getSlotNumber());
        activeTickets.remove(ticket.getVehicle().getRegistrationNumber());
        LocalDate collectionDate = exitTime.toLocalDate();
        Double previous = dailyCollections.get(collectionDate);
        dailyCollections.put(collectionDate, (previous == null ? 0.0 : previous) + fee);
        return fee;
    }

    public double getDailyCollection(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Collection date cannot be null.");
        }
        Double amount = dailyCollections.get(date);
        return amount == null ? 0.0 : amount;
    }

    public ParkingFacility getFacility() {
        return facility;
    }

    private ParkingTicket findActiveTicket(String ticketNumber) {
        for (ParkingTicket ticket : activeTickets.values()) {
            if (ticket.getTicketNumber().equals(ticketNumber)) {
                return ticket;
            }
        }
        return null;
    }
}
