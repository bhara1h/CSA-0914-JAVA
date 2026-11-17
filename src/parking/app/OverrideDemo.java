package parking.app;

import parking.model.Car;

public class OverrideDemo {
    public static void main(String[] args) {
        Car first = new Car("TN22AB1234", "STAFF");
        Car sameRegistration = new Car("TN22AB1234", "VISITOR");
        System.out.println(first);
        System.out.println(first.equals(sameRegistration));
        System.out.println(first.hashCode() == sameRegistration.hashCode());
        System.out.println(first.calculateFee(1));
    }
}
