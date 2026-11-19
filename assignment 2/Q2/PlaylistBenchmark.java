import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PlaylistBenchmark {
    private static final int WARMUPS = 2;
    private static final int REPEATS = 5;
    private static final int[] SIZES = {1_000, 10_000, 100_000};
    private static final List<String> COLLECTIONS = List.of(
            "ArrayList", "LinkedList", "ArrayDeque", "ManualArray");
    private static final List<String> OPERATIONS = List.of(
            "append_end", "insert_front", "insert_middle", "remove_front",
            "remove_middle", "kth_access", "iteration");

    private record Container(String name, List<String> list, Deque<String> deque,
                             ArrayDeque<String> scratch, ManualPlaylist manual) {
    }

    private record Measurement(int size, String collection, String operation, int trial,
                               int operations, long elapsedNs, long checksum) {
    }

    private record Result(long checksum) {
    }

    private static final class ManualPlaylist {
        private String[] elements;
        private int size;

        private ManualPlaylist(int capacity) {
            elements = new String[capacity];
        }

        private int size() {
            return size;
        }

        private String get(int index) {
            checkIndex(index);
            return elements[index];
        }

        private void add(int index, String value) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException(index);
            }
            ensureCapacity(size + 1);
            System.arraycopy(elements, index, elements, index + 1, size - index);
            elements[index] = value;
            size++;
        }

        private String remove(int index) {
            checkIndex(index);
            String removed = elements[index];
            System.arraycopy(elements, index + 1, elements, index, size - index - 1);
            elements[--size] = null;
            return removed;
        }

        private void ensureCapacity(int minimum) {
            if (minimum > elements.length) {
                elements = java.util.Arrays.copyOf(elements, Math.max(minimum, elements.length * 2));
            }
        }

        private void checkIndex(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException(index);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<Measurement> measurements = new ArrayList<>();
        System.out.println("Q2: playlist collection benchmark");
        System.out.printf("Warm-ups per case: %d; measured repeats: %d; source values are identical across collections.%n",
                WARMUPS, REPEATS);
        System.out.println("ArrayDeque middle edits use an auxiliary deque to preserve playlist order; kth access traverses from the nearer end.");
        System.out.println("Initial sample: track-000000, track-000001, track-000002");

        int scenarioIndex = 0;
        for (int size : SIZES) {
            String[] values = createValues(size + 1_000);
            for (String operation : OPERATIONS) {
                int operations = operationsFor(operation);
                for (int trial = -WARMUPS; trial < REPEATS; trial++) {
                    for (int offset = 0; offset < COLLECTIONS.size(); offset++) {
                        String collection = COLLECTIONS.get(Math.floorMod(offset + trial + scenarioIndex,
                                COLLECTIONS.size()));
                        Container container = createContainer(collection, values, size);
                        if (trial < 0) {
                            execute(container, operation, operations, values);
                            continue;
                        }

                        long start = System.nanoTime();
                        Result result = execute(container, operation, operations, values);
                        long elapsed = System.nanoTime() - start;
                        validateSize(container, operation, size, operations);
                        measurements.add(new Measurement(size, collection, operation, trial + 1,
                                operations, elapsed, result.checksum()));
                    }
                }
                printMedians(size, operation, measurements);
                scenarioIndex++;
            }
        }

        Path csv = Path.of("outputs", "Q2.csv");
        Files.createDirectories(csv.getParent());
        writeCsv(csv, measurements);
        System.out.printf("%nRaw measured results: %s (%d rows)%n", csv, measurements.size());
    }

    private static String[] createValues(int count) {
        String[] values = new String[count];
        for (int i = 0; i < count; i++) {
            values[i] = String.format("track-%06d", i);
        }
        return values;
    }

    private static Container createContainer(String name, String[] values, int size) {
        if (name.equals("ArrayList")) {
            List<String> list = new ArrayList<>(size + 1_000);
            for (int i = 0; i < size; i++) {
                list.add(values[i]);
            }
            return new Container(name, list, null, null, null);
        }
        if (name.equals("LinkedList")) {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                list.add(values[i]);
            }
            return new Container(name, list, null, null, null);
        }
        if (name.equals("ManualArray")) {
            ManualPlaylist manual = new ManualPlaylist(size + 1_000);
            for (int i = 0; i < size; i++) {
                manual.add(manual.size(), values[i]);
            }
            return new Container(name, null, null, null, manual);
        }
        Deque<String> deque = new ArrayDeque<>(size + 1_000);
        for (int i = 0; i < size; i++) {
            deque.addLast(values[i]);
        }
        return new Container(name, null, deque, new ArrayDeque<>(size + 1_000), null);
    }

    private static int operationsFor(String operation) {
        return operation.equals("iteration") ? 5 : 100;
    }

    private static Result execute(Container container, String operation, int operations, String[] values) {
        long checksum = 1;
        if (operation.equals("append_end")) {
            for (int i = 0; i < operations; i++) {
                String value = values[i % values.length];
                if (container.list() != null) {
                    container.list().add(value);
                } else if (container.deque() != null) {
                    container.deque().addLast(value);
                } else {
                    container.manual().add(container.manual().size(), value);
                }
                checksum = 31 * checksum + value.hashCode();
            }
        } else if (operation.equals("insert_front")) {
            for (int i = 0; i < operations; i++) {
                String value = values[i % values.length];
                if (container.list() != null) {
                    container.list().add(0, value);
                } else if (container.deque() != null) {
                    container.deque().addFirst(value);
                } else {
                    container.manual().add(0, value);
                }
                checksum = 31 * checksum + value.hashCode();
            }
        } else if (operation.equals("insert_middle")) {
            for (int i = 0; i < operations; i++) {
                String value = values[i % values.length];
                if (container.list() != null) {
                    container.list().add(container.list().size() / 2, value);
                } else if (container.deque() != null) {
                    dequeInsertAt(container.deque(), container.scratch(),
                            container.deque().size() / 2, value);
                } else {
                    container.manual().add(container.manual().size() / 2, value);
                }
                checksum = 31 * checksum + value.hashCode();
            }
        } else if (operation.equals("remove_front")) {
            for (int i = 0; i < operations; i++) {
                String removed = container.list() != null
                        ? container.list().remove(0)
                        : container.deque() != null
                                ? container.deque().removeFirst()
                                : container.manual().remove(0);
                checksum = 31 * checksum + removed.hashCode();
            }
        } else if (operation.equals("remove_middle")) {
            for (int i = 0; i < operations; i++) {
                String removed;
                if (container.list() != null) {
                    removed = container.list().remove(container.list().size() / 2);
                } else if (container.deque() != null) {
                    removed = dequeRemoveAt(container.deque(), container.scratch(),
                            container.deque().size() / 2);
                } else {
                    removed = container.manual().remove(container.manual().size() / 2);
                }
                checksum = 31 * checksum + removed.hashCode();
            }
        } else if (operation.equals("kth_access")) {
            int index = container.list() != null ? container.list().size() / 2
                    : container.deque() != null ? container.deque().size() / 2
                    : container.manual().size() / 2;
            for (int i = 0; i < operations; i++) {
                String value = container.list() != null ? container.list().get(index)
                        : container.deque() != null ? dequeAt(container.deque(), index)
                        : container.manual().get(index);
                checksum = 31 * checksum + value.hashCode();
            }
        } else if (operation.equals("iteration")) {
            for (int pass = 0; pass < operations; pass++) {
                if (container.list() != null) {
                    for (String value : container.list()) {
                        checksum = 31 * checksum + value.hashCode();
                    }
                } else {
                    if (container.deque() != null) {
                        for (String value : container.deque()) {
                            checksum = 31 * checksum + value.hashCode();
                        }
                    } else {
                        for (int i = 0; i < container.manual().size(); i++) {
                            checksum = 31 * checksum + container.manual().get(i).hashCode();
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Unknown operation: " + operation);
        }
        return new Result(checksum);
    }

    private static void dequeInsertAt(Deque<String> deque, ArrayDeque<String> scratch,
                                      int index, String value) {
        for (int i = 0; i < index; i++) {
            scratch.addLast(deque.removeFirst());
        }
        deque.addFirst(value);
        while (!scratch.isEmpty()) {
            deque.addFirst(scratch.removeLast());
        }
    }

    private static String dequeRemoveAt(Deque<String> deque, ArrayDeque<String> scratch, int index) {
        for (int i = 0; i < index; i++) {
            scratch.addLast(deque.removeFirst());
        }
        String removed = deque.removeFirst();
        while (!scratch.isEmpty()) {
            deque.addFirst(scratch.removeLast());
        }
        return removed;
    }

    private static String dequeAt(Deque<String> deque, int index) {
        int fromFront = index;
        int fromBack = deque.size() - 1 - index;
        int hops = Math.min(fromFront, fromBack);
        if (fromFront <= fromBack) {
            var iterator = deque.iterator();
            String value = null;
            for (int i = 0; i <= hops; i++) {
                value = iterator.next();
            }
            return value;
        }
        var iterator = deque.descendingIterator();
        String value = null;
        for (int i = 0; i <= hops; i++) {
            value = iterator.next();
        }
        return value;
    }

    private static void validateSize(Container container, String operation, int initialSize, int operations) {
        int expected = initialSize;
        if (operation.equals("append_end") || operation.equals("insert_front")
                || operation.equals("insert_middle")) {
            expected += operations;
        } else if (operation.equals("remove_front") || operation.equals("remove_middle")) {
            expected -= operations;
        }
        int actual = container.list() != null ? container.list().size()
                : container.deque() != null ? container.deque().size()
                : container.manual().size();
        if (actual != expected) {
            throw new IllegalStateException(container.name() + " " + operation + " size " + actual
                    + "; expected " + expected);
        }
    }

    private static void printMedians(int size, String operation, List<Measurement> measurements) {
        System.out.printf("n=%d %-13s", size, operation);
        for (String collection : COLLECTIONS) {
            List<Long> times = measurements.stream()
                    .filter(m -> m.size() == size && m.operation().equals(operation)
                            && m.collection().equals(collection))
                    .map(Measurement::elapsedNs)
                    .sorted()
                    .toList();
            int ops = measurements.stream()
                    .filter(m -> m.size() == size && m.operation().equals(operation)
                            && m.collection().equals(collection))
                    .findFirst().orElseThrow().operations();
            System.out.printf(" | %s %.2f ns/op", collection,
                    times.get(times.size() / 2) / (double) ops);
        }
        System.out.println();
    }

    private static void writeCsv(Path path, List<Measurement> measurements) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("size,collection,operation,trial,operations_per_trial,elapsed_ns,ns_per_operation,checksum\n");
            for (Measurement m : measurements) {
                writer.write(String.format("%d,%s,%s,%d,%d,%d,%.3f,%d%n",
                        m.size(), m.collection(), m.operation(), m.trial(), m.operations(),
                        m.elapsedNs(), m.elapsedNs() / (double) m.operations(), m.checksum()));
            }
        }
    }
}
