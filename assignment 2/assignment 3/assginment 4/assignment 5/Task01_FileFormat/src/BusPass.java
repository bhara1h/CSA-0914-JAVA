import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class BusPass {
    public static final String FIELD_SEPARATOR = "|";

    private final String passId;
    private final String registerNumber;
    private final String studentName;
    private final int routeNumber;
    private final String boardingStop;
    private final LocalDate validityDate;
    private final double amountPaid;

    public BusPass(String passId, String registerNumber, String studentName,
                   int routeNumber, String boardingStop, LocalDate validityDate,
                   double amountPaid) {
        this.passId = requireText(passId, "Pass ID");
        this.registerNumber = requireText(registerNumber, "Register number");
        this.studentName = requireText(studentName, "Student name");
        this.boardingStop = requireText(boardingStop, "Boarding stop");
        rejectFileSeparators(this.passId, "Pass ID");
        rejectFileSeparators(this.registerNumber, "Register number");
        rejectFileSeparators(this.studentName, "Student name");
        rejectFileSeparators(this.boardingStop, "Boarding stop");

        if (routeNumber <= 0) {
            throw new IllegalArgumentException("Route number must be positive.");
        }
        if (validityDate == null) {
            throw new IllegalArgumentException("Validity date is required.");
        }
        if (!Double.isFinite(amountPaid) || amountPaid < 0) {
            throw new IllegalArgumentException("Amount paid must be a non-negative number.");
        }

        this.routeNumber = routeNumber;
        this.validityDate = validityDate;
        this.amountPaid = amountPaid;
    }

    public static BusPass fromTextLine(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 7) {
            throw new IllegalArgumentException("Expected exactly seven fields.");
        }

        try {
            return new BusPass(
                    fields[0],
                    fields[1],
                    fields[2],
                    Integer.parseInt(fields[3]),
                    fields[4],
                    LocalDate.parse(fields[5]),
                    Double.parseDouble(fields[6]));
        } catch (NumberFormatException | DateTimeParseException exception) {
            throw new IllegalArgumentException("Route, date, or amount is invalid.", exception);
        }
    }

    public String toTextLine() {
        return String.join(FIELD_SEPARATOR,
                passId,
                registerNumber,
                studentName,
                Integer.toString(routeNumber),
                boardingStop,
                validityDate.toString(),
                Double.toString(amountPaid));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private static void rejectFileSeparators(String value, String fieldName) {
        if (value.contains(FIELD_SEPARATOR) || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(fieldName + " cannot contain | or a line break.");
        }
    }

    public String getPassId() {
        return passId;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public String getBoardingStop() {
        return boardingStop;
    }

    public LocalDate getValidityDate() {
        return validityDate;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    @Override
    public String toString() {
        return "BusPass{" +
                "passId='" + passId + '\'' +
                ", registerNumber='" + registerNumber + '\'' +
                ", studentName='" + studentName + '\'' +
                ", routeNumber=" + routeNumber +
                ", boardingStop='" + boardingStop + '\'' +
                ", validityDate=" + validityDate +
                ", amountPaid=" + amountPaid +
                '}';
    }
}
