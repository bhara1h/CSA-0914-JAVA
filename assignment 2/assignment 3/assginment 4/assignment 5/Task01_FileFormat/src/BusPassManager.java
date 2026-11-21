import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class BusPassManager {
    private final Scanner scanner;
    private final BusPassStore store;

    private BusPassManager(Scanner scanner, BusPassStore store) {
        this.scanner = scanner;
        this.store = store;
    }

    public static void main(String[] args) {
        BusPassStore store = new BusPassStore(
                Path.of("data", "bus_passes.txt"),
                Path.of("data", "bus_passes.bin"));
        try (Scanner scanner = new Scanner(System.in)) {
            new BusPassManager(scanner, store).run();
        }
    }

    private void run() {
        boolean running = true;
        while (running) {
            System.out.println("\nBus Pass File Manager");
            System.out.println("1. Add a pass");
            System.out.println("2. Find by pass ID");
            System.out.println("3. List passes by route");
            System.out.println("4. Find binary record using RandomAccessFile index");
            System.out.println("5. Exit");
            int choice = readInteger("Choose an option: ");

            try {
                switch (choice) {
                    case 1:
                        addPass();
                        break;
                    case 2:
                        findPass(false);
                        break;
                    case 3:
                        listByRoute();
                        break;
                    case 4:
                        findPass(true);
                        break;
                    case 5:
                        running = false;
                        break;
                    default:
                        System.out.println("Choose a number from 1 to 5.");
                }
            } catch (IOException exception) {
                System.err.println("File operation failed: " + exception.getMessage());
            } catch (IllegalArgumentException exception) {
                System.err.println("Invalid pass: " + exception.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    private void addPass() throws IOException {
        System.out.println("Store as: 1. Text  2. Binary");
        int format = readFormat();

        String passId = readText("Pass ID: ");
        String registerNumber = readText("Student register number: ");
        String studentName = readText("Student name: ");
        int routeNumber = readInteger("Route number: ");
        String boardingStop = readText("Boarding stop: ");
        LocalDate validityDate = readDate("Validity date (YYYY-MM-DD): ");
        double amountPaid = readAmount("Amount paid: ");

        BusPass pass = new BusPass(passId, registerNumber, studentName, routeNumber,
                boardingStop, validityDate, amountPaid);
        if (format == 1) {
            store.addText(pass);
        } else {
            store.addBinary(pass);
        }
        System.out.println("Pass saved: " + pass);
    }

    private void findPass(boolean indexedBinarySearch) throws IOException {
        int format = 2;
        if (!indexedBinarySearch) {
            System.out.println("Search in: 1. Text  2. Binary");
            format = readFormat();
        } else {
            System.out.println("Searching the binary file using RandomAccessFile.");
        }

        String passId = readText("Pass ID to find: ");
        BusPass pass;
        if (indexedBinarySearch) {
            pass = store.findBinaryByIdUsingIndex(passId);
        } else if (format == 1) {
            pass = store.findTextById(passId);
        } else {
            pass = store.findBinaryById(passId);
        }
        System.out.println(pass == null ? "Pass not found." : pass);
    }

    private void listByRoute() throws IOException {
        System.out.println("List from: 1. Text  2. Binary");
        int format = readFormat();
        int routeNumber = readInteger("Route number: ");
        List<BusPass> passes = format == 1 ? store.readText() : store.readBinary();

        int count = 0;
        for (BusPass pass : passes) {
            if (pass.getRouteNumber() == routeNumber) {
                System.out.println(pass);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No passes found for route " + routeNumber + ".");
        }
    }

    private int readFormat() {
        while (true) {
            int format = readInteger("Format: ");
            if (format == 1 || format == 2) {
                return format;
            }
            System.out.println("Enter 1 for text or 2 for binary.");
        }
    }

    private int readInteger(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private double readAmount(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                double amount = Double.parseDouble(value);
                if (Double.isFinite(amount) && amount >= 0) {
                    return amount;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid non-negative amount.");
                continue;
            }
            System.out.println("Enter a valid non-negative amount.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException exception) {
                System.out.println("Enter a date in YYYY-MM-DD format.");
            }
        }
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
