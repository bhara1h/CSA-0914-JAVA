package parking.app;

import parking.interfaces.Chargeable;
import parking.model.Car;
import parking.model.ElectricCar;
import parking.model.TwoWheeler;
import parking.model.Vehicle;

public class AbstractionDemo {
    public static void main(String[] args) {
        Vehicle[] vehicles = {
            new TwoWheeler("TN01AA", "STUDENT"),
            new Car("TN02BB", "STAFF"),
            new ElectricCar("TN03CC", "VISITOR")
        };
        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle + " -> Rs " + vehicle.calculateFee(60));
            if (vehicle instanceof Chargeable) {
                Chargeable chargeable = (Chargeable) vehicle;
                System.out.println("Charging fee -> Rs " + chargeable.getChargingFee(60));
            }
        }
    }
}
