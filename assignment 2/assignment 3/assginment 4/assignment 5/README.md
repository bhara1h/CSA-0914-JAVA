# CSA09 - Assignment 5: Data Persistence Engineering

**Student:** Bharath M.

**Register number:** [Enter Register Number]

**Java:** 17

**MySQL:** Configure a local MySQL 8.x server for the database tasks.

This assignment is being implemented task by task. Task 1 (text and binary
bus-pass file storage) and Task 2 (buffered and unbuffered file reading) are
implemented. Database-backed tasks will require a local MySQL server and
credentials configured outside source control.

## Folder structure

```text
CSA09-Assignment5/
  Task01_FileFormat/
    src/
    data/
    README.md
  Task02_Buffering/
    src/
    data/test_files/
    results/
    README.md
  Task03_DAO/
  Task04_Serialization/
  Task05_Transactions_Security/
  Task06_PassStore/
  Task07_AdversarialTesting/
  Task08_JDBC_Benchmark/
  Task09_Migration/
  database/
  screenshots/
  README.md
```

## VS Code and completed tasks

Open this folder in VS Code and ensure a Java 17 JDK is selected. Task 1 run,
compile, feature, and test instructions are in
[Task01_FileFormat/README.md](./Task01_FileFormat/README.md).
Task 2 compile, run, and complexity instructions are in
[Task02_Buffering/README.md](./Task02_Buffering/README.md).

## Results and setup status

- Task 1 compiles and runs with the installed Java 17 JDK.
- Task 2 generated 1 MB, 10 MB, and 50 MB test files. Measured three-trial
  medians and the comparison CSV are in `Task02_Buffering/README.md` and
  `Task02_Buffering/results/buffering_results.csv`.
- Database setup and JDBC benchmark results are pending their implementation
  tasks and local MySQL configuration.
- GitHub repository: [Add repository URL]
