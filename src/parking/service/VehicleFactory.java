package parking.service;

import parking.model.Car;
import parking.model.DeliveryVan;
import parking.model.EBike;
import parking.model.ElectricCar;
import parking.model.TwoWheeler;
import parking.model.Vehicle;

public class VehicleFactory {
    public Vehicle create(String type, String registrationNumber, String ownerType) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid vehicle type.");
        }
        switch (type.trim().toUpperCase()) {
            case "TWO_WHEELER":
            case "TWOWHEELER":
                return new TwoWheeler(registrationNumber, ownerType);
            case "CAR":
                return new Car(registrationNumber, ownerType);
            case "ELECTRIC_CAR":
            case "ELECTRICCAR":
                return new ElectricCar(registrationNumber, ownerType);
            case "E_BIKE":
            case "EBIKE":
                return new EBike(registrationNumber, ownerType);
            case "DELIVERY_VAN":
            case "DELIVERYVAN":
                return new DeliveryVan(registrationNumber, ownerType);
            default:
                throw new IllegalArgumentException("Invalid vehicle type: " + type);
        }
    }
}
