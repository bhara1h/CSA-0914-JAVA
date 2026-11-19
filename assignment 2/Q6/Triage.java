import java.util.PriorityQueue;

public class Triage {
    private final PriorityQueue<Patient> waitingPatients =
            new PriorityQueue<>(Q6Demo.PRIORITY_ORDER);

    public void admit(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient is required.");
        }
        waitingPatients.add(patient);
    }

    public Patient serveNext() {
        return waitingPatients.poll();
    }

    public int waitingCount() {
        return waitingPatients.size();
    }
}
