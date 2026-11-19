import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Q1Benchmark {
    private static final int WARMUPS = 2;
    private static final int REPEATS = 5;
    private static final List<String> METHODS = List.of("String +", "StringBuilder", "StringBuffer");

    private record Workload(String name, int ticketCount, int fieldCount) {
    }

    private record Result(String sample, long totalChars, long checksum) {
    }

    private record Measurement(String workload, int tickets, int fields, String method, int trial,
                               long elapsedNs, long outputChars, long checksum,
                               long heapBefore, long heapAfter, long estimatedIntermediates) {
    }

    public static void main(String[] args) throws Exception {
        List<Workload> workloads = List.of(
                new Workload("short", 100, 10),
                new Workload("standard", 10_000, 50),
                new Workload("larger", 12_000, 60));
        List<Measurement> measurements = new ArrayList<>();

        System.out.println("Q1: railway e-ticket construction benchmark");
        System.out.printf("Warm-ups per method/workload: %d; measured repeats: %d%n", WARMUPS, REPEATS);
        System.out.println("Intermediate-string estimate counts aggregate String + results, excluding final retained ticket strings.");

        int workloadIndex = 0;
        for (Workload workload : workloads) {
            String[][] tickets = createTickets(workload);
            long expectedChars = expectedChars(tickets);
            System.out.printf("%nWorkload %s: %d tickets x %d fields (%d total fields)%n",
                    workload.name(), workload.ticketCount(), workload.fieldCount(),
                    (long) workload.ticketCount() * workload.fieldCount());
            System.out.println("Sample ticket: " + construct(tickets[0], "StringBuilder"));

            for (int trial = -WARMUPS; trial < REPEATS; trial++) {
                for (int offset = 0; offset < METHODS.size(); offset++) {
                    String method = METHODS.get(Math.floorMod(offset + trial + workloadIndex, METHODS.size()));
                    if (trial < 0) {
                        run(tickets, method);
                        continue;
                    }

                    System.gc();
                    long heapBefore = usedHeap();
                    long start = System.nanoTime();
                    Result result = run(tickets, method);
                    long elapsed = System.nanoTime() - start;
                    long heapAfter = usedHeap();
                    if (result.totalChars() != expectedChars) {
                        throw new IllegalStateException(method + " produced " + result.totalChars()
                                + " chars; expected " + expectedChars);
                    }
                    measurements.add(new Measurement(workload.name(), workload.ticketCount(),
                            workload.fieldCount(), method, trial + 1, elapsed, result.totalChars(),
                            result.checksum(), heapBefore, heapAfter,
                            method.equals("String +")
                                    ? (long) workload.ticketCount() * (workload.fieldCount() - 1)
                                    : 0));
                }
            }
            printMedians(workload.name(), measurements);
            workloadIndex++;
        }

        Path csv = Path.of("outputs", "Q1.csv");
        Files.createDirectories(csv.getParent());
        writeCsv(csv, measurements);
        System.out.printf("%nRaw measured results: %s (%d rows)%n", csv, measurements.size());
    }

    private static String[][] createTickets(Workload workload) {
        String[][] tickets = new String[workload.ticketCount()][workload.fieldCount()];
        for (int t = 0; t < tickets.length; t++) {
            for (int f = 0; f < tickets[t].length; f++) {
                tickets[t][f] = String.format("T%06d-F%02d=V%08d", t, f, (t * 31 + f) % 100_000_000);
            }
        }
        return tickets;
    }

    private static Result run(String[][] tickets, String method) {
        long totalChars = 0;
        long checksum = 1;
        String sample = "";
        for (int t = 0; t < tickets.length; t++) {
            String ticket = construct(tickets[t], method);
            if (t == 0) {
                sample = ticket;
            }
            totalChars += ticket.length();
            checksum = 31 * checksum + ticket.hashCode();
        }
        return new Result(sample, totalChars, checksum);
    }

    private static String construct(String[] fields, String method) {
        if (method.equals("String +")) {
            return StringComparison.build(fields);
        }
        if (method.equals("StringBuilder")) {
            return StringBuilderComparison.build(fields);
        }
        return StringBufferComparison.build(fields);
    }

    private static long expectedChars(String[][] tickets) {
        long chars = 0;
        for (String[] ticket : tickets) {
            for (String field : ticket) {
                chars += field.length();
            }
            chars += ticket.length - 1L;
        }
        return chars;
    }

    private static long usedHeap() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void printMedians(String workload, List<Measurement> measurements) {
        System.out.println("Median elapsed time (ms):");
        for (String method : METHODS) {
            List<Long> times = measurements.stream()
                    .filter(m -> m.workload().equals(workload) && m.method().equals(method))
                    .map(Measurement::elapsedNs)
                    .sorted(Comparator.naturalOrder())
                    .toList();
            System.out.printf("  %-13s %.3f%n", method, times.get(times.size() / 2) / 1_000_000.0);
        }
    }

    private static void writeCsv(Path path, List<Measurement> measurements) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("workload,tickets,fields_per_ticket,total_fields,method,trial,elapsed_ns,output_chars,checksum,heap_before_bytes,heap_after_bytes,estimated_intermediate_strings\n");
            for (Measurement m : measurements) {
                writer.write(String.format("%s,%d,%d,%d,%s,%d,%d,%d,%d,%d,%d,%d%n",
                        m.workload(), m.tickets(), m.fields(), (long) m.tickets() * m.fields(),
                        m.method(), m.trial(), m.elapsedNs(), m.outputChars(), m.checksum(),
                        m.heapBefore(), m.heapAfter(), m.estimatedIntermediates()));
            }
        }
    }
}
