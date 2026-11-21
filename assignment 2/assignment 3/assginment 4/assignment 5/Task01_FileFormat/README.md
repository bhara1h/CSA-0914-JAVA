# Task 1 - Bus Pass File Formats

This Java 17 program manages bus passes in either a human-readable text file
or a framed binary file. A pass has an ID, register number, student name, route,
boarding stop, validity date, and amount paid.

## Compile and run

From this task folder in PowerShell:

```powershell
New-Item -ItemType Directory -Force out
javac -d out src\BusPass.java src\BusPassStore.java src\BusPassManager.java
java -cp out BusPassManager
```

The program creates `data\bus_passes.txt` or `data\bus_passes.bin` when the
corresponding format is first used. A missing file is treated as an empty store.
Choose an operation from the menu, then choose text or binary where applicable.
Option 4 searches the binary file using `RandomAccessFile`.

## File formats, validation, and recovery

- Text records contain seven `|`-separated fields. `BufferedReader` reads one
  line at a time; lines with an incorrect field count or invalid route, date,
  or amount are reported and skipped. Text values cannot contain `|` or a line
  break.
- Binary records use `DataOutputStream`/`DataInputStream`. Each record has a
  length prefix, so an incomplete final header or payload can be recognized.
  Complete earlier records remain readable; a truncated last record is ignored.
  Before a new binary pass is appended, the incomplete final record (if any) is
  truncated so later valid records remain readable.
- Route numbers must be positive. Amounts must be finite and non-negative.
  Dates use ISO format (`YYYY-MM-DD`).

## Search complexity and RandomAccessFile

Normal text and binary searches scan records from the beginning and take O(n)
time. The indexed option builds a map from pass ID to the binary record's byte
offset using `RandomAccessFile`; index construction is O(n). After construction,
map lookup and seeking directly to a record are average O(1), with O(n) memory
for the in-memory index. Adding a binary pass invalidates that index.

## Suggested checks

1. Add a pass to text, search for it, and list its route.
2. Repeat those operations using binary storage.
3. Search for an ID that does not exist.
4. Manually add a malformed text line to `data\bus_passes.txt`; it should be
   reported and skipped without preventing later valid lines from being read.
5. Back up `data\bus_passes.bin`, append a few arbitrary bytes to it, and list
   route 7. Earlier complete records should still appear. Add another binary
   pass and list again; the incomplete tail should be removed before appending,
   so both earlier and newly added complete records are readable. Restore the
   backup afterward if you want to keep the original sample data.

No measured performance results are claimed for this task; search timing
experiments belong to a later task.
