package parking.app;

import parking.model.Car;
import parking.model.ElectricCar;
import parking.model.TwoWheeler;
import parking.model.Vehicle;

public class FeeDemo {
    public static void main(String[] args) {
        Vehicle twoWheeler = new TwoWheeler("TN01A1", "STUDENT");
        Vehicle car = new Car("TN02B2", "STAFF");
        Vehicle electricCar = new ElectricCar("TN03C3", "VISITOR");

        System.out.println("TwoWheeler TN01A1 -> Rs " + twoWheeler.calculateFee(60));
        System.out.println("Car TN02B2 -> Rs " + car.calculateFee(60));
        System.out.println("ElectricCar TN03C3 -> Rs " + electricCar.calculateFee(60));
        System.out.println(twoWheeler.equals(new TwoWheeler("TN01A1", "VISITOR")));
    }
}
