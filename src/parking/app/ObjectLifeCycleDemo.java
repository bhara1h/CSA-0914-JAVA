package parking.app;

import java.time.LocalDateTime;
import parking.model.Car;
import parking.model.ParkingTicket;
import parking.pricing.StandardPricingRule;
import parking.service.ParkingFacility;
import parking.service.ParkingService;

public class ObjectLifeCycleDemo {
    public static void main(String[] args) {
        ParkingService service = new ParkingService(new ParkingFacility(new int[] {2}),
                new StandardPricingRule());
        LocalDateTime entry = LocalDateTime.now();
        ParkingTicket firstTicket = service.enter(new Car("LIFE01", "STUDENT"), entry, null);
        ParkingTicket secondTicket = service.enter(new Car("LIFE02", "STAFF"), entry, null);

        System.out.println("First ticket: " + firstTicket.getTicketNumber());
        System.out.println("Second ticket: " + secondTicket.getTicketNumber());
        service.exit(firstTicket.getTicketNumber(), entry.plusMinutes(1));
        firstTicket = null;
        System.out.println("First ticket removed from service; local reference cleared.");
        System.out.println("Garbage collection timing is managed by the JVM.");
    }
}
