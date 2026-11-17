package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import parking.model.Car;
import parking.model.ElectricCar;
import parking.model.MonthlyParkingPass;
import parking.model.ParkingTicket;
import parking.model.TwoWheeler;
import parking.pricing.StandardPricingRule;
import parking.service.ParkingFacility;
import parking.service.ParkingService;
import parking.service.VehicleFactory;

public class SystemTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 11, 17, 8, 0);
    private static final List<String[]> RESULTS = new ArrayList<String[]>();

    private static void record(String name, String expected, String actual) {
        RESULTS.add(new String[] {name, expected, actual,
                expected.equals(actual) ? "PASS" : "FAIL"});
    }

    private static String feeAndCapacity(ParkingService service, ParkingTicket ticket,
                                        LocalDateTime exitTime) {
        double fee = service.exit(ticket.getTicketNumber(), exitTime);
        return "fee=" + fee + "; available=" + service.getFacility().getAvailableSlotCount();
    }

    public static void main(String[] args) {
        ParkingService service = new ParkingService(new ParkingFacility(new int[] {2}),
                new StandardPricingRule());
        ParkingTicket ticket = service.enter(new TwoWheeler("TEST01", "STUDENT"), START, null);
        record("Two-wheeler entry and exit", "fee=30.0; available=2",
                feeAndCapacity(service, ticket, START.plusMinutes(30)));

        service = new ParkingService(new ParkingFacility(new int[] {1}), new StandardPricingRule());
        ticket = service.enter(new Car("TEST02", "STAFF"), START, null);
        record("Car entry and exit", "fee=90.0; available=1",
                feeAndCapacity(service, ticket, START.plusMinutes(60)));

        service = new ParkingService(new ParkingFacility(new int[] {1}), new StandardPricingRule());
        ticket = service.enter(new ElectricCar("TEST03", "VISITOR"), START, null);
        record("Electric-car entry and exit", "fee=210.0; available=1",
                feeAndCapacity(service, ticket, START.plusMinutes(60)));

        service = new ParkingService(new ParkingFacility(new int[] {1}), new StandardPricingRule());
        ticket = service.enter(new Car("TEST04", "STUDENT"), START, null);
        record("Last available slot", "ticket issued; available=0",
                (ticket != null ? "ticket issued" : "no ticket") + "; available="
                        + service.getFacility().getAvailableSlotCount());

        try {
            service.enter(new Car("TEST05", "VISITOR"), START, null);
            record("No available slot", "No parking slot is available.", "unexpected allocation");
        } catch (IllegalStateException exception) {
            record("No available slot", "No parking slot is available.", exception.getMessage());
        }

        service = new ParkingService(new ParkingFacility(new int[] {1}), new StandardPricingRule());
        ticket = service.enter(new Car("TEST06", "STUDENT"), START, null);
        record("Zero-minute parking", "0.0",
                Double.toString(service.exit(ticket.getTicketNumber(), START)));

        Car boundaryCar = new Car("TEST07", "STAFF");
        record("Fee slab boundary (60 / 61 minutes)", "90.0 / 180.0",
                boundaryCar.calculateFee(60) + " / " + boundaryCar.calculateFee(61));

        service = new ParkingService(new ParkingFacility(new int[] {2}), new StandardPricingRule());
        service.enter(new Car("DUP01", "STUDENT"), START, null);
        try {
            service.enter(new Car("DUP01", "STAFF"), START, null);
            record("Duplicate registration number", "Vehicle is already parked: DUP01",
                    "duplicate accepted");
        } catch (IllegalStateException exception) {
            record("Duplicate registration number", "Vehicle is already parked: DUP01",
                    exception.getMessage());
        }

        try {
            new ParkingFacility(new int[] {2}).getSlot(0, 2);
            record("Invalid slot number", "Invalid floor or slot number.", "invalid slot accepted");
        } catch (IllegalArgumentException exception) {
            record("Invalid slot number", "Invalid floor or slot number.", exception.getMessage());
        }

        try {
            new VehicleFactory().create("PLANE", "TEST10", "VISITOR");
            record("Invalid vehicle type", "Invalid vehicle type: PLANE", "invalid type accepted");
        } catch (IllegalArgumentException exception) {
            record("Invalid vehicle type", "Invalid vehicle type: PLANE", exception.getMessage());
        }

        service = new ParkingService(new ParkingFacility(new int[] {1}), new StandardPricingRule());
        LocalDate date = START.toLocalDate();
        MonthlyParkingPass pass = new MonthlyParkingPass(date.withDayOfMonth(1),
                date.withDayOfMonth(date.lengthOfMonth()));
        ticket = service.enter(new Car("PASS01", "STAFF"), START, pass);
        record("Monthly pass", "0.0",
                Double.toString(service.exit(ticket.getTicketNumber(), START.plusHours(2))));

        service = new ParkingService(new ParkingFacility(new int[] {2}), new StandardPricingRule());
        ParkingTicket first = service.enter(new TwoWheeler("COL01", "STUDENT"), START, null);
        service.exit(first.getTicketNumber(), START.plusMinutes(1));
        ParkingTicket second = service.enter(new Car("COL02", "STAFF"), START, null);
        service.exit(second.getTicketNumber(), START.plusMinutes(1));
        record("Daily collection calculation", "120.0",
                Double.toString(service.getDailyCollection(START.toLocalDate())));

        System.out.println("Test case | Expected output | Actual output | Status");
        for (String[] result : RESULTS) {
            System.out.println(result[0] + " | " + result[1] + " | " + result[2] + " | "
                    + result[3]);
        }
        for (String[] result : RESULTS) {
            if (!"PASS".equals(result[3])) {
                throw new AssertionError("A system test failed: " + result[0]);
            }
        }
        System.out.println("Passed " + RESULTS.size() + " of " + RESULTS.size() + " tests.");
    }
}
