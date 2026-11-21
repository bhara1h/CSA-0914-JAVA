import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BufferedLineCounter {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java BufferedLineCounter <file-path>");
            return;
        }

        Path file = Path.of(args[0]);
        long startTime = System.nanoTime();
        long lineCount = countLines(file);
        long elapsedNanos = System.nanoTime() - startTime;

        System.out.println("File size: " + Files.size(file) + " bytes");
        System.out.println("Line count: " + lineCount);
        System.out.printf("Buffered execution time: %.3f ms%n", elapsedNanos / 1_000_000.0);
    }

    public static long countLines(Path file) throws IOException {
        long lineCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        }
        return lineCount;
    }
}
