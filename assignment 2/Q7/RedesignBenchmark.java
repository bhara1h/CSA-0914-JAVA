import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RedesignBenchmark {
    private static final int TICKET_COUNT = 10000;
    private static final int FIELD_COUNT = 50;
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 5;

    private interface Workload {
        long run(List<String> fields, int ticketCount);
    }

    private static final class Result {
        final String implementation;
        final int repetition;
        final long elapsedNanos;
        final long heapBefore;
        final long heapAfter;
        final long checksum;

        Result(String implementation, int repetition, long elapsedNanos,
               long heapBefore, long heapAfter, long checksum) {
            this.implementation = implementation;
            this.repetition = repetition;
            this.elapsedNanos = elapsedNanos;
            this.heapBefore = heapBefore;
            this.heapAfter = heapAfter;
            this.checksum = checksum;
        }
    }

    private static long usedHeap() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static List<String> createFields() {
        List<String> fields = new ArrayList<String>();
        for (int i = 0; i < FIELD_COUNT; i++) {
            fields.add("field-" + i + "-railway-seat-");
        }
        return fields;
    }

    private static Result measure(String name, int repetition, Workload workload,
                                  List<String> fields) {
        long before = usedHeap();
        long start = System.nanoTime();
        long checksum = workload.run(fields, TICKET_COUNT);
        long elapsed = System.nanoTime() - start;
        long after = usedHeap();
        return new Result(name, repetition, elapsed, before, after, checksum);
    }

    private static double averageMillis(List<Result> results, String name) {
        long total = 0;
        int count = 0;
        for (Result result : results) {
            if (result.implementation.equals(name) && result.repetition > 0) {
                total += result.elapsedNanos;
                count++;
            }
        }
        return count == 0 ? 0.0 : total / (double) count / 1_000_000.0;
    }

    private static void writeCsv(List<Result> results) throws IOException {
        Path path = Path.of("Q7", "results.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("implementation,repetition,elapsed_ns,elapsed_ms,heap_before_bytes,"
                    + "heap_after_bytes,heap_delta_bytes,tickets,fields_per_ticket,"
                    + "field_appends,checksum");
            writer.newLine();
            for (Result result : results) {
                writer.write(result.implementation + "," + result.repetition + ","
                        + result.elapsedNanos + ","
                        + String.format(java.util.Locale.ROOT, "%.3f",
                                result.elapsedNanos / 1_000_000.0)
                        + "," + result.heapBefore + "," + result.heapAfter + ","
                        + (result.heapAfter - result.heapBefore) + "," + TICKET_COUNT + ","
                        + FIELD_COUNT + "," + ((long) TICKET_COUNT * FIELD_COUNT) + ","
                        + result.checksum);
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> fields = createFields();
        OriginalSolution original = new OriginalSolution();
        RedesignedSolution redesigned = new RedesignedSolution();
        Workload originalWork = original::createTickets;
        Workload redesignedWork = redesigned::createTickets;
        List<Result> results = new ArrayList<Result>();

        if (!original.createTicket(fields).equals(redesigned.createTicket(fields))) {
            throw new IllegalStateException("Ticket contents differ between implementations.");
        }
        for (int run = 0; run < WARMUP_RUNS; run++) {
            originalWork.run(fields, TICKET_COUNT);
            redesignedWork.run(fields, TICKET_COUNT);
        }
        for (int run = 1; run <= MEASURED_RUNS; run++) {
            results.add(measure("StringConcatenation", run, originalWork, fields));
            results.add(measure("StringBuilder", run, redesignedWork, fields));
        }
        writeCsv(results);

        double originalAverage = averageMillis(results, "StringConcatenation");
        double redesignedAverage = averageMillis(results, "StringBuilder");
        double improvement = originalAverage == 0 ? 0
                : (originalAverage - redesignedAverage) * 100.0 / originalAverage;
        String summary = "Design challenge: build " + TICKET_COUNT + " tickets with "
                + FIELD_COUNT + " fields each.\n"
                + "Warm-ups per implementation: " + WARMUP_RUNS + "\n"
                + "Measured repetitions: " + MEASURED_RUNS + "\n"
                + String.format(java.util.Locale.ROOT,
                        "String concatenation average: %.3f ms%n", originalAverage)
                + String.format(java.util.Locale.ROOT,
                        "StringBuilder average: %.3f ms%n", redesignedAverage)
                + String.format(java.util.Locale.ROOT,
                        "Measured time improvement: %.2f%%%n", improvement)
                + "Operations per measured run: " + ((long) TICKET_COUNT * FIELD_COUNT)
                + " field appends per implementation\n"
                + "Correctness: direct ticket strings were compared and are identical.\n"
                + "Memory samples are before/after used-heap snapshots; GC can reclaim objects "
                + "between snapshots.\n";
        Files.writeString(Path.of("Q7", "output.txt"), summary, StandardCharsets.UTF_8);
        System.out.print(summary);
        System.out.println("Raw measurements: Q7/results.csv");
    }
}
