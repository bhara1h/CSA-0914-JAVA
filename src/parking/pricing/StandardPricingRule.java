package parking.pricing;

import java.time.Duration;
import java.time.LocalDateTime;
import parking.model.Vehicle;

public class StandardPricingRule implements PricingRule {
    @Override
    public double calculateFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (vehicle == null || entryTime == null || exitTime == null
                || exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Vehicle and valid parking times are required.");
        }
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        return vehicle.calculateFee(minutes, entryTime);
    }
}
