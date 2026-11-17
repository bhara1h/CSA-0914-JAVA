# CSA09 - Assignment 1: Campus Smart-Parking System

| Student name | Register number | Assignment |
|---|---|---|
| Bharath M. | YOUR_REGISTER_NUMBER | CSA09 - Assignment 1: Campus Smart-Parking System |

**Java version:** Java 17 (source uses standard Java 8+ APIs).

## Project overview
An object-oriented, menu-driven campus parking application. It supports five uneven floors, two-wheelers, cars, electric cars, e-bikes, delivery vans, student/staff/visitor owners, monthly and one-day passes, unique tickets, entry/exit timestamps, vehicle-specific prices, EV fees and daily collection totals. The facility uses a jagged array, the service uses maps for active registrations and daily totals, and vehicle fees use an abstract class, overriding and a separate pricing rule.

Rates for this assignment: two-wheeler Rs 30 per commenced hour; car Rs 90 per commenced hour; electric car car-rate plus Rs 120 per non-zero charging session; e-bike flat Rs 25 per non-zero stay; delivery van car-rate doubled for each billed hour beginning between 09:00 and 17:00. Valid passes cover parking fees. Zero-minute parking is free.

## Folder structure

```text
CSA09-YOUR_REGISTER_NUMBER-Assignment1/
├── README.md
├── Task01/ ... Task12/       Task entry points and task-specific notes
├── src/parking/
│   ├── model/                Vehicle, ticket, slot and passes
│   ├── service/              Facility, service and factory
│   ├── app/                  CLI, demos and test programs
│   ├── interfaces/           Chargeable and pass contracts
│   └── pricing/              Pricing rule abstraction
├── report/                   Task01.md through Task12.md
├── outputs/                  Captured program/test output
└── screenshots/              Reserved for screenshots (see note below)
```

## Compile
From this directory in PowerShell:

```powershell
New-Item -ItemType Directory -Force build | Out-Null
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d build $sources
```

## Run

```powershell
java -cp build parking.app.ParkingApp
java -cp build parking.app.Task03Demo
java -cp build parking.app.FeeDemo
java -cp build parking.app.SystemTest
java -cp build parking.app.DesignChallengeTest
```

Additional demonstrations: `parking.app.ConstructorDemo`, `parking.app.ObjectLifeCycleDemo`, `parking.app.OverrideDemo`, `parking.app.AbstractionDemo`, and `parking.app.WrapperDemo`.

## Task results
1. Problem inputs, outputs, classes, responsibilities, rules, assumptions, alternatives and complexity: [report/Task01.md](report/Task01.md).
2. Java representation, jagged arrays, input, conversions and collection alternatives: [report/Task02.md](report/Task02.md).
3. Jagged allocation/release and exact expected output: [report/Task03.md](report/Task03.md).
4. Domain classes and constructor chaining: [report/Task04.md](report/Task04.md).
5. Static ticket sequence and object lifecycle: [report/Task05.md](report/Task05.md).
6. Inheritance hierarchy, UML and constructor trace: [report/Task06.md](report/Task06.md).
7. Overridden fees, object methods, `final` and `@Override`: [report/Task07.md](report/Task07.md).
8. Overloading/overriding and exact fee demo output: [report/Task08.md](report/Task08.md).
9. Abstract vehicle and chargeable interface: [report/Task09.md](report/Task09.md).
10. Packages, encapsulation, validation and wrappers: [report/Task10.md](report/Task10.md).
11. Complete application and 12/12 passing system checks: [report/Task11.md](report/Task11.md).
12. E-bike, delivery van, one-day pass, five floors and 4/4 passing extension checks: [report/Task12.md](report/Task12.md).

## Verification
Compiled with `javac 17.0.12`. The exact Task 03 and Task 08 outputs and the actual test-run console logs are saved under `outputs/`. `SystemTest` passes 12/12 scenarios; `DesignChallengeTest` passes 4/4. The workspace did not provide a desktop screenshot facility, so screenshots could not be captured; the text outputs are the reproducible execution evidence.

## GitHub
Repository link: `https://github.com/bhara1h/CSA-0914-JAVA`

The `origin` remote is configured for the repository above, and the `main` branch has been pushed. After replacing the student/register-number placeholders, future changes can be uploaded with:

```powershell
git add .
git commit -m "Describe your change"
git push -u origin main
```

The assignment has separate commits for Tasks 01 through 12. Before creating additional commits, configure your own Git author name and email with `git config user.name` and `git config user.email`; the original local commits currently contain the provided register-number email placeholder.
