import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ATM {
    private static final Path ACCOUNT_FILE = Path.of("account.txt");

    static class InsufficientBalanceException extends Exception {
        InsufficientBalanceException(String message) {
            super(message);
        }
    }

    public static double withdraw(String accountNumber, double amount, Path accountFile)
            throws IOException, InsufficientBalanceException {
        if (amount < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative.");
        }

        List<String> lines = Files.readAllLines(accountFile);
        double balance = -1;

        for (String line : lines) {
            String[] parts = line.trim().split(",");
            if (parts.length == 2 && parts[0].trim().equals(accountNumber)) {
                try {
                    balance = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Account balance is not valid numeric text.");
                }
                break;
            }
        }

        if (balance < 0) {
            throw new IOException("Account number was not found in account.txt.");
        }
        if (amount > balance) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: " + String.format("%.2f", balance));
        }

        double remaining = balance - amount;
        String updatedLine = accountNumber + "," + String.format("%.2f", remaining);
        StringBuilder updatedFile = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(accountNumber + ",")) {
                updatedFile.append(updatedLine).append(System.lineSeparator());
            } else {
                updatedFile.append(line).append(System.lineSeparator());
            }
        }
        Files.writeString(accountFile, updatedFile.toString());
        return remaining;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.next();

            System.out.print("Enter withdrawal amount: ");
            if (!scanner.hasNextDouble()) {
                System.out.println("Error: Invalid text input. Enter a numeric amount.");
                return;
            }
            double amount = scanner.nextDouble();

            double remaining = withdraw(accountNumber, amount, ACCOUNT_FILE);
            System.out.printf("Withdrawal successful. Remaining balance: %.2f%n", remaining);
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input type.");
        } catch (NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: Could not read or update account file.");
            System.out.println("Details: " + e.getMessage());
        } finally {
            System.out.println("Finally: ATM attempt completed.");
            scanner.close();
        }
    }

    public static int withdrawWithErrorCode(Scanner scanner) {
        System.out.print("Enter account number: ");
        if (!scanner.hasNext()) {
            return 301;
        }
        String accountNumber = scanner.next();

        System.out.print("Enter withdrawal amount: ");
        if (!scanner.hasNextInt()) {
            return 302;
        }
        int amount = scanner.nextInt();
        if (amount < 0) {
            return 303;
        }

        try {
            List<String> lines = Files.readAllLines(ACCOUNT_FILE);
            for (String line : lines) {
                String[] parts = line.trim().split(",");
                if (parts.length == 2 && parts[0].trim().equals(accountNumber)) {
                    int balance = Integer.parseInt(parts[1].trim());
                    if (amount > balance) {
                        return 304;
                    }
                    return 200;
                }
            }
            return 305;
        } catch (Exception e) {
            return 306;
        }
    }
}
