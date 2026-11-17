package parking.interfaces;

import java.time.LocalDate;

public interface ParkingPass {
    boolean isValid(LocalDate date);

    double applyTo(double parkingFee);

    String getDescription();
}
