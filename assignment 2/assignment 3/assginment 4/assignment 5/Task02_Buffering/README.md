# Task 2 - Buffered and Unbuffered File Reading

This task compares two line counters. `UnbufferedLineCounter` reads one byte at
a time through `FileInputStream`; `BufferedLineCounter` reads complete lines
through `BufferedReader`. `FileReadingBenchmark` generates 1 MB, 10 MB, and
50 MB test files, checks that both implementations count the same number of
lines, measures three trials for each approach, and records the median times.

## Compile

From this task folder in PowerShell:

```powershell
New-Item -ItemType Directory -Force out
javac -d out src\UnbufferedLineCounter.java src\BufferedLineCounter.java src\FileReadingBenchmark.java
```

## Run the benchmark

```powershell
java -cp out FileReadingBenchmark
```

Generated files are stored in `data\test_files\`. Measured results are written
to `results\buffering_results.csv`. The benchmark prints file size in bytes,
line count, median unbuffered and buffered times in milliseconds, and the
unbuffered-to-buffered speed-up. The files are exactly 1,000,000,
10,000,000, and 50,000,000 bytes.

To run either line counter independently:

```powershell
java -cp out UnbufferedLineCounter data\test_files\1MB.txt
java -cp out BufferedLineCounter data\test_files\1MB.txt
```

## Complexity

Both approaches examine every byte/character, so their time complexity is
O(n), where n is the file size. The unbuffered implementation uses O(1)
additional application memory while issuing a read operation per byte.
`BufferedReader` uses O(b) memory for its fixed-size read buffer (plus the
current line returned by `readLine`) and reduces read-call overhead by loading
data in chunks. Operating-system file caching and machine load can affect
measured times, so the CSV contains measurements from the current run rather
than guaranteed performance values.

## Results

Measured on the current Java 17 environment. Each result is the median of
three trials; file sizes are exact bytes. Times and speed-up are machine- and
cache-dependent.

| File size | Lines | Unbuffered median (ms) | Buffered median (ms) | Speed-up |
|---:|---:|---:|---:|---:|
| 1,000,000 bytes | 12,500 | 1,782.106 | 13.796 | 129.18x |
| 10,000,000 bytes | 125,000 | 17,040.032 | 33.673 | 506.04x |
| 50,000,000 bytes | 625,000 | 87,751.430 | 106.872 | 821.09x |

The full precision saved by the benchmark is in
`results\buffering_results.csv`. Standalone counters and the benchmark were
run successfully. Both counters also returned five lines for a small test
containing CRLF, CR, LF, a blank line, and a final line without a terminator.

**Suggested screenshot:** Capture the benchmark's printed comparison table and
the generated `results\buffering_results.csv`.

**Suggested commit:** `feat(task-2): benchmark buffered and unbuffered file reading`
