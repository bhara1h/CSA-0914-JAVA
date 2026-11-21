# CSA09 - Assignment 4

Six independent Java desktop programs demonstrating event-driven GUI
development with AWT and Swing. Tasks 1-5 use only the Java standard library.
Task 6 uses SQLite through JDBC.

## Requirements

- JDK 17 or newer (`java` and `javac` on `PATH`)
- VS Code with the **Extension Pack for Java** for editor-based run/debug support
- For Task 6: SQLite command-line tools and the SQLite JDBC runtime dependencies

## Project structure

```text
CSA09_Assignment4/
|-- Task1_FeePayment/
|-- Task2_HospitalAppointment/
|-- Task3_LibrarySearch/
|-- Task4_DrawPad/
|-- Task5_TemperatureConverter/
|-- Task6_CampusEventRegistration/
|-- database/
|   `-- campus_event_registration.sql
|-- screenshots/
`-- README.md
```

Each task has its own main class and can be compiled and run independently.
The `screenshots` directory is intentionally empty: the programs create live
interfaces rather than using static screenshots.

## Compile and run Tasks 1-5

Open a PowerShell terminal in the directory containing `CSA09_Assignment4`.
For each task, change into its folder, compile to `out`, then run its main
class. For example, Task 1:

```powershell
Set-Location .\CSA09_Assignment4\Task1_FeePayment
New-Item -ItemType Directory -Force .\out | Out-Null
$sources = Get-ChildItem -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d .\out $sources
java -cp .\out FeePaymentApp
```

Use the corresponding folder and main class for the other tasks:

| Task | Folder | Main class |
|---|---|---|
| 1 - Fee Payment | `Task1_FeePayment` | `FeePaymentApp` |
| 2 - Hospital Appointment | `Task2_HospitalAppointment` | `HospitalAppointmentApp` |
| 3 - Library Search | `Task3_LibrarySearch` | `LibrarySearchApp` |
| 4 - DrawPad (AWT) | `Task4_DrawPad` | `DrawPad` |
| 5 - Temperature Converter | `Task5_TemperatureConverter` | `TemperatureConverterApp` |

For example, after changing to Task 2's folder, run the same compile commands
and then `java -cp .\out HospitalAppointmentApp`. The same pattern works for
Tasks 3-5 with their main class names above.

In VS Code, open the `CSA09_Assignment4` folder, open the task's main `.java`
file, and select **Run Java** above its `main` method. Tasks 1-5 need no
additional libraries.

## Task 6 - database setup and run

The database URL is relative to the assignment folder, so run Task 6's Java
command from the directory containing `CSA09_Assignment4`.

1. Install SQLite command-line tools and make `sqlite3` available in
   PowerShell.
2. Create the database and table by running:

   ```powershell
   sqlite3 .\CSA09_Assignment4\database\campus_events.db ".read .\CSA09_Assignment4\database\campus_event_registration.sql"
   ```

3. Download the SQLite JDBC driver and its SLF4J runtime dependencies:

   ```powershell
   New-Item -ItemType Directory -Force .\lib | Out-Null
   Invoke-WebRequest "https://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar" -OutFile .\lib\sqlite-jdbc-3.45.3.0.jar
   Invoke-WebRequest "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar" -OutFile .\lib\slf4j-api-2.0.13.jar
   Invoke-WebRequest "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-nop/2.0.13/slf4j-nop-2.0.13.jar" -OutFile .\lib\slf4j-nop-2.0.13.jar
   ```

4. Compile and launch:

   ```powershell
   $driver = ".\lib\sqlite-jdbc-3.45.3.0.jar;.\lib\slf4j-api-2.0.13.jar;.\lib\slf4j-nop-2.0.13.jar"
   $sources = Get-ChildItem .\CSA09_Assignment4\Task6_CampusEventRegistration -Filter *.java | ForEach-Object { $_.FullName }
   New-Item -ItemType Directory -Force .\CSA09_Assignment4\Task6_CampusEventRegistration\out | Out-Null
   javac -encoding UTF-8 -d .\CSA09_Assignment4\Task6_CampusEventRegistration\out $sources
   java -cp ".\CSA09_Assignment4\Task6_CampusEventRegistration\out;$driver" CampusEventRegistrationApp
   ```

The registration form validates required fields, email, and phone. Duplicate
registrations for the same register number and event are rejected by the
database's unique constraint. Search matches student, register number,
department, email, and event. Results are loaded in pages of 50 using
`SwingWorker`; registration and database reads run off the Swing event thread.
The layers are `CampusEventRegistrationApp` (presentation),
`RegistrationService` (validation/service), `RegistrationDao` (prepared SQL),
and `DatabaseConnection` plus the SQL script (database).

## Task 5 listener approaches

The converter offers four conversion buttons so each listener pattern is
directly observable:

1. **Implements `ActionListener`** - `TemperatureConverterApp` implements the
   interface; the first button registers `this`.
2. **Anonymous inner class** - the second button defines its listener inline.
3. **Lambda expression** - the third button uses a concise lambda listener.
4. **Separate listener class** - the fourth button registers
   `SeparateTemperatureListener`, a top-level class in the same source file.

All four call the same conversion logic. The first approach suits a class with
a clear listener role, the anonymous class keeps a one-off listener near its
button, a lambda is concise for a single-method listener, and a separate class
keeps listener behavior outside the UI class.

## Manual test cases

- **Task 1 - Fee Payment:** submit valid details and each payment method;
  leave a required field blank; enter alphabetic, zero, or negative fee values.
- **Task 2 - Hospital Appointment:** book a future appointment; change
  department and verify the doctor choices; try missing patient details,
  malformed date/time, and a past date; select and cancel a booking.
- **Task 3 - Library Search:** search by title, author, and ISBN; search for
  an absent term and confirm the no-results message; clear results and refresh
  the catalog; resize the window.
- **Task 4 - DrawPad:** draw with press/drag/release; draw a single click;
  resize/minimize and restore the window; clear the canvas.
- **Task 5 - Temperature Converter:** convert `0`, `100`, and a decimal
  Celsius value with each of the four buttons; try blank, non-numeric,
  `NaN`, and infinite input; clear both fields.
- **Task 6 - Campus Event Registration:** register valid student details;
  try blank fields, invalid email, and invalid phone; register the same
  student/event twice; filter by student, register number, and event; add
  more than 50 records and test Next/Previous; restart and verify persistence.

## Screenshots

Run the relevant application, arrange the window, and capture it with the
Windows Snipping Tool (`Win` + `Shift` + `S`) or VS Code's screen capture
workflow. Save the captured image in `CSA09_Assignment4\screenshots` with a
descriptive name, such as `task1-fee-payment.png`. Capture the running
application itself; no screenshots are supplied in place of the interfaces.

## Verification note

All six task source sets were compiled with JDK 17. The Task 6 JDBC service
was integration-smoke-tested against the provided schema for insert, filtering,
pagination, duplicate rejection, and invalid-email rejection. The desktop
windows were not visually exercised; follow the setup steps above to run them.
