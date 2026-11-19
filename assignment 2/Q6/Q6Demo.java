import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;

public final class Q6Demo {
    static final Comparator<Patient> PRIORITY_ORDER =
            Comparator.comparingInt(Patient::getSeverity)
                    .thenComparingLong(Patient::getArrival);

    private Q6Demo() {
    }

    public static void main(String[] args) {
        List<Patient> sixPatients = List.of(
                new Patient("Meena", 1, 0),
                new Patient("Kumar", 1, 1),
                new Patient("Lata", 2, 2),
                new Patient("Ravi", 3, 3),
                new Patient("John", 3, 4),
                new Patient("Asha", 5, 5));

        List<String> expected = List.of(
                "Meena(S1)", "Kumar(S1)", "Lata(S2)",
                "Ravi(S3)", "John(S3)", "Asha(S5)");
        for (String implementation : List.of("PriorityQueue", "Sorted LinkedList", "Five FIFO queues")) {
            QueueModel queue = createQueue(implementation);
            sixPatients.forEach(queue::admit);
            List<String> actual = drain(queue);
            require(actual.equals(expected), implementation + " produced " + actual);
            if (implementation.equals("PriorityQueue")) {
                System.out.println(String.join(" ", actual));
            } else {
                System.out.println(implementation + " output: " + String.join(" ", actual));
            }
        }

        verifyEightPatientHandCheck();
        System.out.println("Eight-patient hand-check passed (including FIFO within severity).");
        System.out.println("All Q6 checks passed.");
    }

    private static void verifyEightPatientHandCheck() {
        List<Patient> patients = List.of(
                new Patient("Meena", 1, 0),
                new Patient("Kumar", 1, 1),
                new Patient("Lata", 2, 2),
                new Patient("Ravi", 3, 3),
                new Patient("John", 3, 4),
                new Patient("Asha", 5, 5),
                new Patient("Nisha", 1, 6),
                new Patient("Omar", 3, 7));
        List<String> expected = List.of(
                "Meena(S1)", "Kumar(S1)", "Nisha(S1)", "Lata(S2)",
                "Ravi(S3)", "John(S3)", "Omar(S3)", "Asha(S5)");
        for (String implementation : List.of("PriorityQueue", "Sorted LinkedList", "Five FIFO queues")) {
            QueueModel queue = createQueue(implementation);
            patients.forEach(queue::admit);
            require(drain(queue).equals(expected), implementation + " failed the eight-patient check");
        }
    }

    static QueueModel createQueue(String name) {
        switch (name) {
            case "PriorityQueue":
                return new PriorityQueueModel();
            case "Sorted LinkedList":
                return new SortedLinkedListModel();
            case "Five FIFO queues":
                return new FiveQueuesModel();
            default:
                throw new IllegalArgumentException("Unknown implementation: " + name);
        }
    }

    static List<String> drain(QueueModel queue) {
        List<String> result = new ArrayList<>();
        Patient next;
        while ((next = queue.poll()) != null) {
            result.add(next.toString());
        }
        return result;
    }

    static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    interface QueueModel {
        void admit(Patient patient);

        Patient poll();
    }

    static final class PriorityQueueModel implements QueueModel {
        private final PriorityQueue<Patient> queue = new PriorityQueue<>(PRIORITY_ORDER);

        @Override
        public void admit(Patient patient) {
            queue.add(patient);
        }

        @Override
        public Patient poll() {
            return queue.poll();
        }
    }

    static final class SortedLinkedListModel implements QueueModel {
        private final java.util.LinkedList<Patient> queue = new java.util.LinkedList<>();

        @Override
        public void admit(Patient patient) {
            ListIterator<Patient> iterator = queue.listIterator();
            while (iterator.hasNext()) {
                if (Q6Demo.PRIORITY_ORDER.compare(iterator.next(), patient) > 0) {
                    iterator.previous();
                    iterator.add(patient);
                    return;
                }
            }
            iterator.add(patient);
        }

        @Override
        public Patient poll() {
            return queue.isEmpty() ? null : queue.removeFirst();
        }
    }

    static final class FiveQueuesModel implements QueueModel {
        private final List<Queue<Patient>> queues = List.of(
                new ArrayDeque<>(), new ArrayDeque<>(), new ArrayDeque<>(),
                new ArrayDeque<>(), new ArrayDeque<>());

        @Override
        public void admit(Patient patient) {
            queues.get(patient.getSeverity() - 1).add(patient);
        }

        @Override
        public Patient poll() {
            for (Queue<Patient> queue : queues) {
                Patient patient = queue.poll();
                if (patient != null) {
                    return patient;
                }
            }
            return null;
        }
    }
}
