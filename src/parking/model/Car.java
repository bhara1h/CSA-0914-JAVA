package parking.model;

public class Car extends Vehicle {
    public Car(String registrationNumber, String ownerType) {
        super(registrationNumber, ownerType);
    }

    @Override
    public double calculateFee(long parkedMinutes) {
        validateMinutes(parkedMinutes);
        return billedHours(parkedMinutes) * 90.0;
    }
}
