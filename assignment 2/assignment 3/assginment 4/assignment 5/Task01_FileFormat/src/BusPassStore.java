import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusPassStore {
    private static final int MAX_RECORD_SIZE = 1_000_000;

    private final Path textFile;
    private final Path binaryFile;
    private Map<String, Long> binaryIndex;

    public BusPassStore(Path textFile, Path binaryFile) {
        this.textFile = textFile;
        this.binaryFile = binaryFile;
    }

    public void addText(BusPass pass) throws IOException {
        createParentDirectory(textFile);
        try (BufferedWriter writer = Files.newBufferedWriter(
                textFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(pass.toTextLine());
            writer.newLine();
        }
    }

    public void addBinary(BusPass pass) throws IOException {
        byte[] record = encode(pass);
        createParentDirectory(binaryFile);
        recoverIncompleteBinaryTail();
        try (DataOutputStream output = new DataOutputStream(
                Files.newOutputStream(binaryFile,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND))) {
            output.writeInt(record.length);
            output.write(record);
        }
        binaryIndex = null;
    }

    private void recoverIncompleteBinaryTail() throws IOException {
        if (!Files.exists(binaryFile)) {
            return;
        }

        try (RandomAccessFile file = new RandomAccessFile(binaryFile.toFile(), "rw")) {
            while (file.getFilePointer() < file.length()) {
                long recordOffset = file.getFilePointer();
                if (file.length() - recordOffset < Integer.BYTES) {
                    file.setLength(recordOffset);
                    break;
                }

                int recordLength = file.readInt();
                if (recordLength <= 0 || recordLength > MAX_RECORD_SIZE
                        || file.length() - file.getFilePointer() < recordLength) {
                    file.setLength(recordOffset);
                    break;
                }
                file.seek(file.getFilePointer() + recordLength);
            }
        }
    }

    public List<BusPass> readText() throws IOException {
        List<BusPass> passes = new ArrayList<>();
        if (!Files.exists(textFile)) {
            return passes;
        }

        try (BufferedReader reader = Files.newBufferedReader(textFile, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    passes.add(BusPass.fromTextLine(line));
                } catch (IllegalArgumentException exception) {
                    System.err.println("Skipping damaged text record at line " + lineNumber
                            + ": " + exception.getMessage());
                }
            }
        }
        return passes;
    }

    public List<BusPass> readBinary() throws IOException {
        List<BusPass> passes = new ArrayList<>();
        if (!Files.exists(binaryFile)) {
            return passes;
        }

        try (DataInputStream input = new DataInputStream(Files.newInputStream(binaryFile))) {
            while (true) {
                int recordLength;
                try {
                    recordLength = input.readInt();
                } catch (EOFException exception) {
                    break;
                }
                if (recordLength <= 0 || recordLength > MAX_RECORD_SIZE) {
                    System.err.println("Stopping at an invalid binary record length.");
                    break;
                }

                byte[] record = new byte[recordLength];
                try {
                    input.readFully(record);
                } catch (EOFException exception) {
                    System.err.println("Ignoring incomplete final binary record.");
                    break;
                }

                try {
                    passes.add(decode(record));
                } catch (IOException | IllegalArgumentException exception) {
                    System.err.println("Skipping damaged binary record: " + exception.getMessage());
                }
            }
        }
        return passes;
    }

    public BusPass findTextById(String passId) throws IOException {
        for (BusPass pass : readText()) {
            if (pass.getPassId().equals(passId)) {
                return pass;
            }
        }
        return null;
    }

    public BusPass findBinaryById(String passId) throws IOException {
        for (BusPass pass : readBinary()) {
            if (pass.getPassId().equals(passId)) {
                return pass;
            }
        }
        return null;
    }

    public BusPass findBinaryByIdUsingIndex(String passId) throws IOException {
        if (binaryIndex == null) {
            buildBinaryIndex();
        }
        Long recordOffset = binaryIndex.get(passId);
        if (recordOffset == null) {
            return null;
        }

        try (RandomAccessFile file = new RandomAccessFile(binaryFile.toFile(), "r")) {
            file.seek(recordOffset);
            int recordLength = file.readInt();
            if (recordLength <= 0 || recordLength > MAX_RECORD_SIZE) {
                throw new IOException("Indexed record has an invalid length.");
            }
            byte[] record = new byte[recordLength];
            file.readFully(record);
            return decode(record);
        }
    }

    private void buildBinaryIndex() throws IOException {
        binaryIndex = new HashMap<>();
        if (!Files.exists(binaryFile)) {
            return;
        }

        try (RandomAccessFile file = new RandomAccessFile(binaryFile.toFile(), "r")) {
            while (file.getFilePointer() < file.length()) {
                long recordOffset = file.getFilePointer();
                if (file.length() - recordOffset < Integer.BYTES) {
                    System.err.println("Ignoring incomplete final binary record header.");
                    break;
                }

                int recordLength = file.readInt();
                if (recordLength <= 0 || recordLength > MAX_RECORD_SIZE) {
                    System.err.println("Stopping index scan at an invalid binary record length.");
                    break;
                }
                if (file.length() - file.getFilePointer() < recordLength) {
                    System.err.println("Ignoring incomplete final binary record.");
                    break;
                }

                byte[] record = new byte[recordLength];
                file.readFully(record);
                try {
                    BusPass pass = decode(record);
                    binaryIndex.put(pass.getPassId(), recordOffset);
                } catch (IOException | IllegalArgumentException exception) {
                    System.err.println("Skipping damaged binary record while indexing: "
                            + exception.getMessage());
                }
            }
        }
    }

    private static byte[] encode(BusPass pass) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeUTF(pass.getPassId());
            output.writeUTF(pass.getRegisterNumber());
            output.writeUTF(pass.getStudentName());
            output.writeInt(pass.getRouteNumber());
            output.writeUTF(pass.getBoardingStop());
            output.writeUTF(pass.getValidityDate().toString());
            output.writeDouble(pass.getAmountPaid());
        }
        return bytes.toByteArray();
    }

    private static BusPass decode(byte[] record) throws IOException {
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(record))) {
            String passId = input.readUTF();
            String registerNumber = input.readUTF();
            String studentName = input.readUTF();
            int routeNumber = input.readInt();
            String boardingStop = input.readUTF();
            LocalDate validityDate = LocalDate.parse(input.readUTF());
            double amountPaid = input.readDouble();
            if (input.available() != 0) {
                throw new IOException("Binary record contains extra data.");
            }
            return new BusPass(passId, registerNumber, studentName, routeNumber,
                    boardingStop, validityDate, amountPaid);
        } catch (RuntimeException exception) {
            throw new IOException("Binary record contains invalid field values.", exception);
        }
    }

    private static void createParentDirectory(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
