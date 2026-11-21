import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Locale;

public class FileReadingBenchmark {
    private static final long[] FILE_SIZES = {
            1_000_000L,
            10_000_000L,
            50_000_000L
    };
    private static final int TRIALS = 3;
    private static final int LINE_LENGTH = 80;

    public static void main(String[] args) throws IOException {
        Path dataDirectory = Path.of("data", "test_files");
        Path resultsDirectory = Path.of("results");
        Files.createDirectories(dataDirectory);
        Files.createDirectories(resultsDirectory);

        Path csvFile = resultsDirectory.resolve("buffering_results.csv");
        try (BufferedWriter csv = Files.newBufferedWriter(
                csvFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            csv.write("file_size_bytes,line_count,unbuffered_median_ms,buffered_median_ms,speed_up,trials");
            csv.newLine();

            System.out.printf("%-12s %-14s %-22s %-20s %s%n",
                    "File size", "Line count", "Unbuffered (ms)",
                    "Buffered (ms)", "Speed-up");

            for (long fileSize : FILE_SIZES) {
                Path testFile = dataDirectory.resolve(fileSize / 1_000_000 + "MB.txt");
                generateTestFile(testFile, fileSize);

                long[] unbufferedTimes = new long[TRIALS];
                long[] bufferedTimes = new long[TRIALS];
                long expectedLineCount = -1;

                for (int trial = 0; trial < TRIALS; trial++) {
                    if (trial % 2 == 0) {
                        long start = System.nanoTime();
                        long lines = UnbufferedLineCounter.countLines(testFile);
                        unbufferedTimes[trial] = System.nanoTime() - start;
                        expectedLineCount = verifyLineCount(expectedLineCount, lines);

                        start = System.nanoTime();
                        lines = BufferedLineCounter.countLines(testFile);
                        bufferedTimes[trial] = System.nanoTime() - start;
                        expectedLineCount = verifyLineCount(expectedLineCount, lines);
                    } else {
                        long start = System.nanoTime();
                        long lines = BufferedLineCounter.countLines(testFile);
                        bufferedTimes[trial] = System.nanoTime() - start;
                        expectedLineCount = verifyLineCount(expectedLineCount, lines);

                        start = System.nanoTime();
                        lines = UnbufferedLineCounter.countLines(testFile);
                        unbufferedTimes[trial] = System.nanoTime() - start;
                        expectedLineCount = verifyLineCount(expectedLineCount, lines);
                    }
                }

                long unbufferedMedian = median(unbufferedTimes);
                long bufferedMedian = median(bufferedTimes);
                double speedUp = (double) unbufferedMedian / bufferedMedian;
                long actualFileSize = Files.size(testFile);

                System.out.printf(Locale.ROOT, "%-12d %-14d %-22.3f %-20.3f %.2fx%n",
                        actualFileSize,
                        expectedLineCount,
                        unbufferedMedian / 1_000_000.0,
                        bufferedMedian / 1_000_000.0,
                        speedUp);
                csv.write(String.format(Locale.ROOT, "%d,%d,%.3f,%.3f,%.3f,%d",
                        actualFileSize,
                        expectedLineCount,
                        unbufferedMedian / 1_000_000.0,
                        bufferedMedian / 1_000_000.0,
                        speedUp,
                        TRIALS));
                csv.newLine();
            }
        }

        System.out.println("Generated test files in " + dataDirectory);
        System.out.println("Saved measurements to " + csvFile);
    }

    private static void generateTestFile(Path file, long sizeInBytes) throws IOException {
        byte[] chunk = new byte[64 * 1024];
        long position = 0;

        try (BufferedOutputStream output = new BufferedOutputStream(
                Files.newOutputStream(file,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
            while (position < sizeInBytes) {
                int bytesToWrite = (int) Math.min(chunk.length, sizeInBytes - position);
                for (int index = 0; index < bytesToWrite; index++) {
                    chunk[index] = (position + index + 1) % LINE_LENGTH == 0
                            ? (byte) '\n'
                            : (byte) 'A';
                }
                output.write(chunk, 0, bytesToWrite);
                position += bytesToWrite;
            }
        }
    }

    private static long verifyLineCount(long expected, long actual) {
        if (expected >= 0 && expected != actual) {
            throw new IllegalStateException(
                    "Line counters disagree: expected " + expected + ", got " + actual);
        }
        return actual;
    }

    private static long median(long[] values) {
        long[] sortedValues = Arrays.copyOf(values, values.length);
        Arrays.sort(sortedValues);
        return sortedValues[sortedValues.length / 2];
    }
}
