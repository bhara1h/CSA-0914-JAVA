package parking.model;

public class TwoWheeler extends Vehicle {
    public TwoWheeler() {
        this("UNKNOWN", "VISITOR");
    }

    public TwoWheeler(String registrationNumber) {
        this(registrationNumber, "VISITOR");
    }

    public TwoWheeler(String registrationNumber, String ownerType) {
        super(registrationNumber, ownerType);
    }

    @Override
    public double calculateFee(long parkedMinutes) {
        validateMinutes(parkedMinutes);
        return billedHours(parkedMinutes) * 30.0;
    }
}
