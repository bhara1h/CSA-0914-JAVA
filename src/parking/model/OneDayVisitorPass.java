package parking.model;

import java.time.LocalDate;
import parking.interfaces.ParkingPass;

public class OneDayVisitorPass implements ParkingPass {
    private final LocalDate validDate;

    public OneDayVisitorPass(LocalDate validDate) {
        if (validDate == null) {
            throw new IllegalArgumentException("Visitor pass date cannot be null.");
        }
        this.validDate = validDate;
    }

    @Override
    public boolean isValid(LocalDate date) {
        return validDate.equals(date);
    }

    @Override
    public double applyTo(double parkingFee) {
        return parkingFee >= 0.0 ? 0.0 : parkingFee;
    }

    @Override
    public String getDescription() {
        return "One-day visitor pass";
    }
}
