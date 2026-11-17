package parking.pricing;

import java.time.LocalDateTime;
import parking.model.Vehicle;

public interface PricingRule {
    double calculateFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime);
}
