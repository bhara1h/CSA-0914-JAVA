import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnbufferedLineCounter {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java UnbufferedLineCounter <file-path>");
            return;
        }

        Path file = Path.of(args[0]);
        long startTime = System.nanoTime();
        long lineCount = countLines(file);
        long elapsedNanos = System.nanoTime() - startTime;

        System.out.println("File size: " + Files.size(file) + " bytes");
        System.out.println("Line count: " + lineCount);
        System.out.printf("Unbuffered execution time: %.3f ms%n", elapsedNanos / 1_000_000.0);
    }

    public static long countLines(Path file) throws IOException {
        long lineCount = 0;
        boolean previousWasCarriageReturn = false;
        boolean hasCharactersSinceLineEnd = false;

        try (FileInputStream input = new FileInputStream(file.toFile())) {
            int value;
            while ((value = input.read()) != -1) {
                if (value == '\r') {
                    lineCount++;
                    previousWasCarriageReturn = true;
                    hasCharactersSinceLineEnd = false;
                } else if (value == '\n') {
                    if (!previousWasCarriageReturn) {
                        lineCount++;
                    }
                    previousWasCarriageReturn = false;
                    hasCharactersSinceLineEnd = false;
                } else {
                    previousWasCarriageReturn = false;
                    hasCharactersSinceLineEnd = true;
                }
            }
        }

        if (hasCharactersSinceLineEnd) {
            lineCount++;
        }
        return lineCount;
    }
}
