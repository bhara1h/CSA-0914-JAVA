import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class InventoryBenchmark {
    private static final int[] SIZES = {1_000, 10_000, 100_000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 5;
    private static final int LOOKUP_COUNT = 100_000;
    private static final long LOOKUP_SLA_NS = 100_000_000L;
    private static volatile long sink;

    public static void main(String[] args) throws IOException {
        Path csvPath = args.length > 0 ? Paths.get(args[0])
                : Paths.get("outputs", "Q4_benchmark.csv");
        Files.createDirectories(csvPath.toAbsolutePath().getParent());
        Path reportsPath = csvPath.toAbsolutePath().getParent()
                .resolve("Q4_inventory_reports.txt");
        writeInventoryReports(reportsPath);

        List<Trial> trials = new ArrayList<>();
        try (BufferedWriter writer = Files.newBufferedWriter(
                csvPath, StandardCharsets.UTF_8)) {
            writer.write("size,collection,trial,put_total_ns,get_same_key_100k_ns,"
                    + "mixed_lookups_100k_ns,iteration_ns,entries_after_put\n");

            for (int size : SIZES) {
                List<Medicine> dataset = createDataset(size);
                int[] lookupIndexes = createLookupIndexes(size);
                for (String collection : collectionNames()) {
                    for (int warmup = 0; warmup < WARMUP_RUNS; warmup++) {
                        runTrial(size, collection, -warmup - 1, dataset, lookupIndexes);
                    }
                    for (int trialNumber = 1; trialNumber <= MEASURED_RUNS; trialNumber++) {
                        Trial trial = runTrial(size, collection, trialNumber,
                                dataset, lookupIndexes);
                        trials.add(trial);
                        writer.write(trial.csvRow());
                        writer.newLine();
                    }
                }
            }
        }

        System.out.println("Medicine inventory benchmark");
        System.out.println("Warmups per size/collection: " + WARMUP_RUNS
                + "; measured repetitions: " + MEASURED_RUNS);
        System.out.println("Each mixed lookup trial: " + LOOKUP_COUNT + " successful lookups");
        System.out.println("Median mixed-lookup SLA: <= 100 ms per 100,000 lookups");
        System.out.println("CSV: " + csvPath.toAbsolutePath());
        System.out.println("Sorted/insertion reports: " + reportsPath);
        printSummaries(trials);
        System.out.println("Recommendation: prefer HashMap for code-keyed inventory when "
                + "sorted output is not required; use TreeMap when sorted-by-code reports "
                + "are a primary requirement, or LinkedHashMap for insertion-order reports.");
        System.out.println("sink=" + sink);
    }

    private static Trial runTrial(int size, String collection, int trialNumber,
                                  List<Medicine> dataset, int[] lookupIndexes) {
        Inventory emptyInventory = new Inventory(collection);
        long start = System.nanoTime();
        for (Medicine medicine : dataset) {
            emptyInventory.put(medicine);
        }
        long putNs = System.nanoTime() - start;
        int entriesAfterPut = emptyInventory.size();

        Inventory inventory = new Inventory(collection);
        for (Medicine medicine : dataset) {
            inventory.put(medicine);
        }

        long checksum = 0;
        Medicine fixedKeyResult;
        String fixedCode = dataset.get(size / 2).getCode();
        start = System.nanoTime();
        for (int i = 0; i < LOOKUP_COUNT; i++) {
            fixedKeyResult = inventory.get(fixedCode);
            checksum += fixedKeyResult == null ? 0 : fixedKeyResult.getQuantity();
        }
        long fixedGetNs = System.nanoTime() - start;

        start = System.nanoTime();
        for (int index : lookupIndexes) {
            Medicine medicine = inventory.get(dataset.get(index).getCode());
            checksum += medicine == null ? 0 : medicine.getQuantity();
        }
        long mixedLookupNs = System.nanoTime() - start;

        start = System.nanoTime();
        for (Medicine medicine : inventory.values()) {
            checksum += medicine.getCode().hashCode();
            checksum += medicine.getQuantity();
        }
        long iterationNs = System.nanoTime() - start;
        sink = sink + checksum;

        return new Trial(size, collection, trialNumber, putNs, fixedGetNs,
                mixedLookupNs, iterationNs, entriesAfterPut);
    }

    private static List<Medicine> createDataset(int size) {
        List<Medicine> dataset = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            dataset.add(new Medicine(String.format("MED%06d", i),
                    "Medicine-" + i, 20 + i % 500, 1.25 + (i % 20_000) / 100.0));
        }
        Collections.shuffle(dataset, new Random(9_140L + size));
        return dataset;
    }

    private static void writeInventoryReports(Path path) throws IOException {
        List<Medicine> reportDataset = createDataset(10);
        Inventory insertionOrder = new Inventory("LinkedHashMap");
        Inventory sortedOrder = new Inventory("TreeMap");
        for (Medicine medicine : reportDataset) {
            insertionOrder.put(medicine);
            sortedOrder.put(medicine);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("Inventory report sample (same 10 records, same insertion sequence)");
            writer.newLine();
            writer.write("Insertion order (LinkedHashMap):");
            writer.newLine();
            writeInventory(writer, insertionOrder);
            writer.newLine();
            writer.write("Sorted by medicine code (TreeMap):");
            writer.newLine();
            writeInventory(writer, sortedOrder);
        }
    }

    private static void writeInventory(BufferedWriter writer, Inventory inventory)
            throws IOException {
        writer.write("code,name,quantity,price");
        writer.newLine();
        for (Medicine medicine : inventory.values()) {
            writer.write(medicine.getCode() + "," + medicine.getName() + ","
                    + medicine.getQuantity() + "," + medicine.getPrice());
            writer.newLine();
        }
    }

    private static int[] createLookupIndexes(int size) {
        int[] indexes = new int[LOOKUP_COUNT];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = (int) ((i * 7_919L + 17) % size);
        }
        return indexes;
    }

    private static String[] collectionNames() {
        return new String[]{"HashMap", "TreeMap", "LinkedHashMap"};
    }

    private static void printSummaries(List<Trial> trials) {
        for (int size : SIZES) {
            for (String collection : collectionNames()) {
                List<Trial> group = new ArrayList<>();
                for (Trial trial : trials) {
                    if (trial.size == size && trial.collection.equals(collection)) {
                        group.add(trial);
                    }
                }
                long putMedian = median(group, Metric.PUT);
                long fixedMedian = median(group, Metric.FIXED_GET);
                long mixedMedian = median(group, Metric.MIXED_LOOKUP);
                long iterationMedian = median(group, Metric.ITERATION);
                System.out.printf(
                        "%s n=%d median: put=%d ns (%.1f ns/entry), "
                                + "get=%.1f ns/op, 100k lookups=%d ns (%.1f ns/op), "
                                + "iteration=%d ns; SLA=%s%n",
                        collection, size, putMedian, putMedian / (double) size,
                        fixedMedian / (double) LOOKUP_COUNT, mixedMedian,
                        mixedMedian / (double) LOOKUP_COUNT, iterationMedian,
                        mixedMedian <= LOOKUP_SLA_NS ? "PASS" : "FAIL");
            }
        }
    }

    private static long median(List<Trial> group, Metric metric) {
        List<Long> values = new ArrayList<>();
        for (Trial trial : group) {
            switch (metric) {
                case PUT:
                    values.add(trial.putNs);
                    break;
                case FIXED_GET:
                    values.add(trial.fixedGetNs);
                    break;
                case MIXED_LOOKUP:
                    values.add(trial.mixedLookupNs);
                    break;
                case ITERATION:
                    values.add(trial.iterationNs);
                    break;
                default:
                    throw new IllegalStateException("Unknown metric");
            }
        }
        Collections.sort(values);
        return values.get(values.size() / 2);
    }

    private enum Metric {
        PUT, FIXED_GET, MIXED_LOOKUP, ITERATION
    }

    private static final class Inventory {
        private final Map<String, Medicine> medicines;

        private Inventory(String collection) {
            if ("TreeMap".equals(collection)) {
                medicines = new TreeMap<>();
            } else if ("LinkedHashMap".equals(collection)) {
                medicines = new LinkedHashMap<>();
            } else {
                medicines = new HashMap<>();
            }
        }

        private void put(Medicine medicine) {
            medicines.put(medicine.getCode(), medicine);
        }

        private Medicine get(String code) {
            return medicines.get(code);
        }

        private Iterable<Medicine> values() {
            return medicines.values();
        }

        private int size() {
            return medicines.size();
        }
    }

    private static final class Trial {
        private final int size;
        private final String collection;
        private final int trialNumber;
        private final long putNs;
        private final long fixedGetNs;
        private final long mixedLookupNs;
        private final long iterationNs;
        private final int entriesAfterPut;

        private Trial(int size, String collection, int trialNumber, long putNs,
                      long fixedGetNs, long mixedLookupNs, long iterationNs,
                      int entriesAfterPut) {
            this.size = size;
            this.collection = collection;
            this.trialNumber = trialNumber;
            this.putNs = putNs;
            this.fixedGetNs = fixedGetNs;
            this.mixedLookupNs = mixedLookupNs;
            this.iterationNs = iterationNs;
            this.entriesAfterPut = entriesAfterPut;
        }

        private String csvRow() {
            return size + "," + collection + "," + trialNumber + "," + putNs + ","
                    + fixedGetNs + "," + mixedLookupNs + "," + iterationNs + ","
                    + entriesAfterPut;
        }
    }
}
