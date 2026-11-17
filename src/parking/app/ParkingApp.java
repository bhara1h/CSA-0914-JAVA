package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Scanner;
import parking.interfaces.ParkingPass;
import parking.model.MonthlyParkingPass;
import parking.model.OneDayVisitorPass;
import parking.model.ParkingTicket;
import parking.model.Vehicle;
import parking.pricing.StandardPricingRule;
import parking.service.ParkingFacility;
import parking.service.ParkingService;
import parking.service.VehicleFactory;

public class ParkingApp {
    public static void main(String[] args) {
        ParkingService service = new ParkingService(new ParkingFacility(),
                new StandardPricingRule());
        VehicleFactory factory = new VehicleFactory();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Campus Smart-Parking System");
        while (running) {
            System.out.println("\n1. Vehicle entry  2. Vehicle exit  3. Availability"
                    + "  4. Daily collection  0. Quit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Vehicle type (TWO_WHEELER/CAR/ELECTRIC_CAR"
                                + "/E_BIKE/DELIVERY_VAN): ");
                        String type = scanner.nextLine();
                        System.out.print("Registration number: ");
                        String registration = scanner.nextLine();
                        System.out.print("Owner (STUDENT/STAFF/VISITOR): ");
                        String owner = scanner.nextLine();
                        Vehicle vehicle = factory.create(type, registration, owner);
                        ParkingPass pass = readPass(scanner);
                        ParkingTicket ticket = service.enter(vehicle, LocalDateTime.now(), pass);
                        System.out.println("Entry accepted: " + ticket);
                        break;
                    case "2":
                        System.out.print("Ticket number: ");
                        String ticketNumber = scanner.nextLine();
                        double fee = service.exit(ticketNumber, LocalDateTime.now());
                        System.out.println("Exit accepted. Fee: Rs " + fee);
                        break;
                    case "3":
                        System.out.println("Available slots: "
                                + service.getFacility().getAvailableSlotCount() + " / "
                                + service.getFacility().getTotalSlotCount());
                        break;
                    case "4":
                        System.out.print("Collection date (YYYY-MM-DD): ");
                        LocalDate date = LocalDate.parse(scanner.nextLine());
                        System.out.println("Daily collection: Rs "
                                + service.getDailyCollection(date));
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Choose one of the listed menu options.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
        scanner.close();
        System.out.println("Parking system closed.");
    }

    private static ParkingPass readPass(Scanner scanner) {
        System.out.print("Pass (0 none, 1 monthly, 2 one-day visitor): ");
        String choice = scanner.nextLine().trim();
        LocalDate today = LocalDate.now();
        if ("1".equals(choice)) {
            YearMonth month = YearMonth.from(today);
            return new MonthlyParkingPass(month.atDay(1), month.atEndOfMonth());
        }
        if ("2".equals(choice)) {
            return new OneDayVisitorPass(today);
        }
        if ("0".equals(choice)) {
            return null;
        }
        throw new IllegalArgumentException("Invalid pass option.");
    }
}
