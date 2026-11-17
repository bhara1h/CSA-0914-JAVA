package parking.model;

import java.time.LocalDateTime;

public class DeliveryVan extends Vehicle {
    private static final double CAR_RATE_PER_BILLED_HOUR = 90.0;
    private static final double PEAK_RATE_MULTIPLIER = 2.0;

    public DeliveryVan(String registrationNumber, String ownerType) {
        super(registrationNumber, ownerType);
    }

    @Override
    public double calculateFee(long parkedMinutes) {
        validateMinutes(parkedMinutes);
        return billedHours(parkedMinutes) * CAR_RATE_PER_BILLED_HOUR;
    }

    @Override
    public double calculateFee(long parkedMinutes, LocalDateTime entryTime) {
        validateMinutes(parkedMinutes);
        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time cannot be null.");
        }
        long hours = billedHours(parkedMinutes);
        double total = 0.0;
        for (long hour = 0; hour < hours; hour++) {
            LocalDateTime billedHour = entryTime.plusHours(hour);
            boolean peak = billedHour.getHour() >= 9 && billedHour.getHour() < 17;
            total += CAR_RATE_PER_BILLED_HOUR * (peak ? PEAK_RATE_MULTIPLIER : 1.0);
        }
        return total;
    }
}
