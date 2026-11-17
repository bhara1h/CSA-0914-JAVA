package parking.model;

import java.time.LocalDate;
import parking.interfaces.ParkingPass;

public class MonthlyParkingPass implements ParkingPass {
    private final LocalDate validFrom;
    private final LocalDate validThrough;

    public MonthlyParkingPass(LocalDate validFrom, LocalDate validThrough) {
        if (validFrom == null || validThrough == null || validThrough.isBefore(validFrom)) {
            throw new IllegalArgumentException("Monthly pass dates are invalid.");
        }
        this.validFrom = validFrom;
        this.validThrough = validThrough;
    }

    @Override
    public boolean isValid(LocalDate date) {
        return date != null && !date.isBefore(validFrom) && !date.isAfter(validThrough);
    }

    @Override
    public double applyTo(double parkingFee) {
        return isNonNegative(parkingFee) ? 0.0 : parkingFee;
    }

    @Override
    public String getDescription() {
        return "Monthly pass";
    }

    private boolean isNonNegative(double fee) {
        return fee >= 0.0;
    }
}
