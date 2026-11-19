import java.util.Objects;

public final class Patient {
    private final String name;
    private final int severity;
    private final long arrival;

    public Patient(String name, int severity, long arrival) {
        this.name = Objects.requireNonNull(name, "name");
        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException("Severity must be from 1 to 5");
        }
        if (arrival < 0) {
            throw new IllegalArgumentException("Arrival counter cannot be negative");
        }
        this.severity = severity;
        this.arrival = arrival;
    }

    public String getName() {
        return name;
    }

    public int getSeverity() {
        return severity;
    }

    public long getArrival() {
        return arrival;
    }

    @Override
    public String toString() {
        return name + "(S" + severity + ")";
    }
}
