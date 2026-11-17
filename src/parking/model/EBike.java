package parking.model;

import parking.interfaces.Chargeable;

public class EBike extends Vehicle implements Chargeable {
    private static final double FLAT_PARKING_FEE = 25.0;

    public EBike(String registrationNumber, String ownerType) {
        super(registrationNumber, ownerType);
    }

    @Override
    public double calculateFee(long parkedMinutes) {
        validateMinutes(parkedMinutes);
        return parkedMinutes == 0 ? 0.0 : FLAT_PARKING_FEE;
    }

    @Override
    public double getChargingFee(long parkedMinutes) {
        validateMinutes(parkedMinutes);
        return 0.0;
    }
}
