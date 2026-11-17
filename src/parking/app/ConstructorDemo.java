package parking.app;

import parking.model.TwoWheeler;

public class ConstructorDemo {
    public static void main(String[] args) {
        TwoWheeler defaultVehicle = new TwoWheeler();
        TwoWheeler chainedVehicle = new TwoWheeler("TN10X1");
        TwoWheeler fullVehicle = new TwoWheeler("TN11Y2", "STAFF");

        System.out.println("Default constructor: " + defaultVehicle);
        System.out.println("One-argument constructor: " + chainedVehicle
                + ", owner=" + chainedVehicle.getOwnerType());
        System.out.println("Parameterized constructor: " + fullVehicle
                + ", owner=" + fullVehicle.getOwnerType());
        System.out.println("Constructor order: Vehicle constructor runs before TwoWheeler body.");
    }
}
