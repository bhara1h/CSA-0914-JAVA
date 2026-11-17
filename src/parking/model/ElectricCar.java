package parking.model;

import parking.interfaces.Chargeable;

public class ElectricCar extends Car implements Chargeable {
    private static final double CHARGING_FEE_PER_SESSION = 120.0;

    public ElectricCar(String registrationNumber, String ownerType) {
        super(registrationNumber, ownerType);
    }

    @Override
    public double calculateFee(long parkedMinutes) {
        validateMinutes(parkedMinutes);
        return parkedMinutes == 0 ? 0.0
                : super.calculateFee(parkedMinutes) + getChargingFee(parkedMinutes);
    }

    @Override
    public double getChargingFee(long parkedMinutes) {
        validateMinutes(parkedMinutes);
        return parkedMinutes == 0 ? 0.0 : CHARGING_FEE_PER_SESSION;
    }
}
