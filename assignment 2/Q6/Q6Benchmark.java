import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Q6Benchmark {
    private static final int[] SIZES = {1_000, 10_000, 100_000};
    private static final int WARMUP_REPETITIONS = 3;
    private static final int MEASURED_REPETITIONS = 7;
    private static final int MAX_RESIDENT_PATIENTS = 256;
    private static final String[] IMPLEMENTATIONS = {
            "PriorityQueue", "Sorted LinkedList", "Five FIFO queues"
    };

    private Q6Benchmark() {
    }

    public static void main(String[] args) throws IOException {
        Path output = args.length == 0
                ? Path.of("outputs", "Q6-benchmark.csv")
                : Path.of(args[0]);
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("implementation,patients,peak_resident,warmups,measured_repetitions,"
                    + "trial,admission_ms,removal_ms,total_ms,operations_each\n");
            for (int size : SIZES) {
                List<Patient> data = createData(size, 20261117L + size);
                for (String implementation : IMPLEMENTATIONS) {
                    for (int i = 0; i < WARMUP_REPETITIONS; i++) {
                        run(data, implementation);
                    }

                    long admissionTotal = 0;
                    long removalTotal = 0;
                    for (int trial = 1; trial <= MEASURED_REPETITIONS; trial++) {
                        Timing timing = run(data, implementation);
                        admissionTotal += timing.admissionNanos;
                        removalTotal += timing.removalNanos;
                        double admissionMs = nanosToMillis(timing.admissionNanos);
                        double removalMs = nanosToMillis(timing.removalNanos);
                        writer.write(String.format(java.util.Locale.ROOT,
                                "%s,%d,%d,%d,%d,%d,%.6f,%.6f,%.6f,%d%n",
                                implementation, size, Math.min(size, MAX_RESIDENT_PATIENTS),
                                WARMUP_REPETITIONS, MEASURED_REPETITIONS, trial,
                                admissionMs, removalMs, admissionMs + removalMs, size));
                        writer.flush();
                    }
                    double admissionMs = nanosToMillis(admissionTotal / (double) MEASURED_REPETITIONS);
                    double removalMs = nanosToMillis(removalTotal / (double) MEASURED_REPETITIONS);
                    System.out.printf(java.util.Locale.ROOT,
                            "%s n=%d admission=%.3f ms removal=%.3f ms%n",
                            implementation, size, admissionMs, removalMs);
                }
            }
        }
        System.out.println("CSV written to " + output.toAbsolutePath());
    }

    private static List<Patient> createData(int size, long seed) {
        Random random = new Random(seed);
        List<Patient> patients = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int severity = 1 + random.nextInt(5);
            patients.add(new Patient("P" + i, severity, i));
        }
        return Collections.unmodifiableList(patients);
    }

    private static Timing run(List<Patient> data, String implementation) {
        Q6Demo.QueueModel queue = Q6Demo.createQueue(implementation);
        long admissionNanos = 0;
        long removalNanos = 0;
        int next = 0;
        while (next < data.size()) {
            int end = Math.min(next + MAX_RESIDENT_PATIENTS, data.size());
            long start = System.nanoTime();
            for (int i = next; i < end; i++) {
                queue.admit(data.get(i));
            }
            admissionNanos += System.nanoTime() - start;

            start = System.nanoTime();
            for (int i = next; i < end; i++) {
                if (queue.poll() == null) {
                    throw new AssertionError("Queue became empty before the batch was drained");
                }
            }
            removalNanos += System.nanoTime() - start;
            next = end;
        }
        if (queue.poll() != null) {
            throw new AssertionError("Queue should be empty at the end of a run");
        }
        return new Timing(admissionNanos, removalNanos);
    }

    private static double nanosToMillis(double nanos) {
        return nanos / 1_000_000.0;
    }

    private static final class Timing {
        private final long admissionNanos;
        private final long removalNanos;

        private Timing(long admissionNanos, long removalNanos) {
            this.admissionNanos = admissionNanos;
            this.removalNanos = removalNanos;
        }
    }
}
