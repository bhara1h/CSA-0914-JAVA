package parking.model;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Vehicle {
    private final String registrationNumber;
    private final String ownerType;

    protected Vehicle(String registrationNumber, String ownerType) {
        this.registrationNumber = requireText(registrationNumber, "Registration number");
        this.ownerType = requireText(ownerType, "Owner type");
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim().toUpperCase();
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public abstract double calculateFee(long parkedMinutes);

    public double calculateFee(int hours, int extraMinutes) {
        if (hours < 0 || extraMinutes < 0) {
            throw new IllegalArgumentException("Parking time cannot be negative.");
        }
        return calculateFee(hours * 60L + extraMinutes);
    }

    public double calculateFee(long parkedMinutes, LocalDateTime entryTime) {
        return calculateFee(parkedMinutes);
    }

    protected static long billedHours(long parkedMinutes) {
        if (parkedMinutes < 0) {
            throw new IllegalArgumentException("Parking time cannot be negative.");
        }
        return parkedMinutes == 0 ? 0 : (parkedMinutes + 59) / 60;
    }

    protected static void validateMinutes(long parkedMinutes) {
        if (parkedMinutes < 0) {
            throw new IllegalArgumentException("Parking time cannot be negative.");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + registrationNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Vehicle vehicle = (Vehicle) other;
        return registrationNumber.equals(vehicle.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), registrationNumber);
    }
}
