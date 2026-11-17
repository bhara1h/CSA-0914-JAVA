package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import parking.model.DeliveryVan;
import parking.model.EBike;
import parking.model.OneDayVisitorPass;
import parking.model.ParkingTicket;
import parking.pricing.StandardPricingRule;
import parking.service.ParkingFacility;
import parking.service.ParkingService;

public class DesignChallengeTest {
    private static void check(String name, String expected, String actual) {
        String status = expected.equals(actual) ? "PASS" : "FAIL";
        System.out.println(name + " | expected=" + expected + " | actual=" + actual
                + " | " + status);
        if ("FAIL".equals(status)) {
            throw new AssertionError(name);
        }
    }

    public static void main(String[] args) {
        LocalDate day = LocalDate.of(2026, 11, 17);
        LocalDateTime start = day.atTime(10, 0);
        ParkingService service = new ParkingService(new ParkingFacility(new int[] {1, 2, 1, 3, 2}),
                new StandardPricingRule());
        check("E-bike flat fee", "25.0",
                Double.toString(service.exit(service.enter(new EBike("EB01", "STUDENT"), start,
                        null).getTicketNumber(), start.plusHours(4))));

        ParkingTicket van = service.enter(new DeliveryVan("VAN01", "STAFF"), start, null);
        check("Delivery van peak rate", "180.0",
                Double.toString(service.exit(van.getTicketNumber(), start.plusHours(1))));

        OneDayVisitorPass pass = new OneDayVisitorPass(day);
        ParkingTicket visitor = service.enter(new EBike("VIS01", "VISITOR"), start, pass);
        check("One-day visitor pass", "0.0",
                Double.toString(service.exit(visitor.getTicketNumber(), start.plusHours(1))));
        check("Five floors with uneven slot counts", "5 floors / 9 slots",
                service.getFacility().getFloorCount() + " floors / "
                        + service.getFacility().getTotalSlotCount() + " slots");
    }
}
