# CSA09 - Assignment 2 (CO2)

| Field | Value |
|---|---|
| Assignment | Java Strings, StringBuilder, Regular Expressions and Collection Framework – Experimental Analysis and Design |
| Student | Bharath M. |
| Register number | 192324302 |
| Course | CSA09 |
| Language | Java 17 |
| Repository | https://github.com/bhara1h/CSA-0914-JAVA/tree/main/assignment%202 |

## Project overview
Seven independent Java exercises explore text construction, playlist data structures, equality and hashing, map implementations, generic ranking, triage queues and evidence-driven redesign. Benchmark programs use warm-ups, repeated `System.nanoTime()` measurements, shared input data and CSV output. Reports discuss results and asymptotic complexity. Measurements are specific to this machine and JVM; they are not universal performance guarantees.

## Project structure

```text
assignment 2/
├── Q1/                         String, StringBuilder and StringBuffer
├── Q2/                         Playlist collection comparison
├── Q3/                         Student equality, hashing and attendance
├── Q4/                         Pharmacy inventory maps
├── Q5/                         Generic leaderboard
├── Q6/                         Hospital triage queues
├── Q7/                         Original and redesigned ticket generation
├── report/                     Q1.md through Q7.md
├── experimental-data/          Consolidated question CSV results
├── graphs/                     Benchmark comparison charts
└── outputs/                    Captured program output
```

Each question has its own Java sources, raw `results.csv` and captured `output.txt`. Benchmark CSVs include units in their column names. No execution screenshots were captured in this terminal environment; the text outputs and raw results are provided for verification.

## Compile and run
Run the following PowerShell commands from this folder. Each question is compiled independently so same-named demo classes in different folders do not conflict.

```powershell
foreach ($q in 1..7) {
    $folder = "Q$q"
    New-Item -ItemType Directory -Force "$folder\classes" | Out-Null
    $sources = Get-ChildItem "$folder" -Filter *.java | ForEach-Object { $_.FullName }
    javac -d "$folder\classes" $sources
    if ($LASTEXITCODE -ne 0) { throw "Compilation failed for $folder" }
}
```

Run the main program(s) for each question:

```powershell
java -cp Q1\classes StringBenchmark
java -cp Q2\classes CollectionBenchmark
java -cp Q2\classes PlaylistDemo
java -cp Q3\classes Attendance
java -cp Q3\classes StudentCollectionsDemo
java -cp Q4\classes MapBenchmark Q4\results.csv
java -cp Q5\classes LeaderboardDemo
java -cp Q5\classes ComparatorDemo
java -cp Q6\classes PriorityQueueDemo
java -cp Q6\classes ArrayQueueDemo
java -cp Q6\classes TriageDemo
java -cp Q6\classes TriageBenchmark Q6\results.csv
java -cp Q7\classes RedesignBenchmark
```

Use the benchmark main class to regenerate that question's CSV data. Benchmark results can vary with CPU load, Java runtime, JIT compilation and garbage collection.

## Question results
- **Q1:** At 10,000 tickets x 50 fields, median concatenation time was 42.719 ms vs 16.128 ms for `StringBuilder`; see [report/Q1.md](report/Q1.md).
- **Q2:** At 100,000 tracks, front insertion was 16 ns/op for `ArrayDeque` vs 5,564 ns/op for `ArrayList`; middle edits favor array-backed lists; see [report/Q2.md](report/Q2.md).
- **Q3:** Corrected attendance reports exactly `Unique students: 2`; see [report/Q3.md](report/Q3.md).
- **Q4:** `HashMap` completed 100,000 mixed lookups in a 6.880 ms median at 100,000 records; see [report/Q4.md](report/Q4.md).
- **Q5:** Required ranking passed: `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`; see [report/Q5.md](report/Q5.md).
- **Q6:** The exact six-patient service order and eight-patient FIFO tie check passed; see [report/Q6.md](report/Q6.md).
- **Q7:** `StringBuilder` reduced average time by 86.44% for 10,000 tickets x 50 fields; see [report/Q7.md](report/Q7.md).

## GitHub
This assignment is stored under the existing repository's `assignment 2` directory. After configuring your own Git author identity, commit each question separately and push the branch:

```powershell
git config user.name "Bharath M."
git config user.email "YOUR_EMAIL"
git add "assignment 2/Q1" "assignment 2/report/Q1.md"
git commit -m "Complete Q1 String Benchmark"
git push origin main
```

Repeat with the corresponding question folder and report for Q2-Q7. Replace `YOUR_EMAIL` before final submission.
