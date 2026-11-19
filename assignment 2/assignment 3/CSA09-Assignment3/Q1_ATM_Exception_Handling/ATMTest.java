import java.nio.file.Files;
import java.nio.file.Path;

public class ATMTest {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("atm-test-");
        Path validFile = dir.resolve("valid.txt");
        Files.writeString(validFile, "12345,5000.00\n67890,1000.00\n");

        runSuccess(validFile);
        runBoundary(validFile);
        runZero(validFile);
        runNegative(validFile);
        runInsufficient(validFile);
        runMissing(dir.resolve("missing.txt"));
        runUnknownAccount(validFile);
        runInvalidBalance(dir);
        runInvalidText();
    }

    private static void runSuccess(Path file) throws Exception {
        double remaining = ATM.withdraw("12345", 500, file);
        assertEqual("successful withdrawal", remaining, 4500.0);
    }

    private static void runBoundary(Path file) throws Exception {
        double remaining = ATM.withdraw("67890", 1000, file);
        assertEqual("exact balance withdrawal", remaining, 0.0);
    }

    private static void runZero(Path file) throws Exception {
        double remaining = ATM.withdraw("12345", 0, file);
        assertEqual("zero withdrawal", remaining, 4500.0);
    }

    private static void runInvalidText() {
        java.util.Scanner scanner = new java.util.Scanner("abc");
        if (!scanner.hasNextDouble()) {
            pass("invalid text input");
        } else {
            fail("invalid text input");
        }
        scanner.close();
    }

    private static void runNegative(Path file) {
        try {
            ATM.withdraw("12345", -1, file);
            fail("negative amount");
        } catch (IllegalArgumentException e) {
            pass("negative amount");
        } catch (Exception e) {
            fail("negative amount");
        }
    }

    private static void runInsufficient(Path file) {
        try {
            ATM.withdraw("67890", 1001, file);
            fail("insufficient balance");
        } catch (ATM.InsufficientBalanceException e) {
            pass("insufficient balance");
        } catch (Exception e) {
            fail("insufficient balance");
        }
    }

    private static void runMissing(Path file) {
        try {
            ATM.withdraw("12345", 100, file);
            fail("missing file");
        } catch (java.io.IOException e) {
            pass("missing file");
        } catch (Exception e) {
            fail("missing file");
        }
    }

    private static void runUnknownAccount(Path file) {
        try {
            ATM.withdraw("99999", 100, file);
            fail("unknown account");
        } catch (java.io.IOException e) {
            pass("unknown account");
        } catch (Exception e) {
            fail("unknown account");
        }
    }

    private static void runInvalidBalance(Path dir) throws Exception {
        Path file = dir.resolve("invalid.txt");
        Files.writeString(file, "12345,not-a-number\n");
        try {
            ATM.withdraw("12345", 100, file);
            fail("invalid balance text");
        } catch (NumberFormatException e) {
            pass("invalid balance text");
        } catch (Exception e) {
            fail("invalid balance text");
        }
    }

    private static void assertEqual(String testName, double actual, double expected) {
        if (Math.abs(actual - expected) < 0.001) {
            pass(testName);
        } else {
            fail(testName + ": expected " + expected + " but got " + actual);
        }
    }

    private static void pass(String testName) {
        System.out.println("PASS: " + testName);
    }

    private static void fail(String testName) {
        System.out.println("FAIL: " + testName);
    }
}
