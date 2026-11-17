# Task 11 - Complete System Implementation and Testing

## Aim
Run the entry/exit lifecycle, pricing, pass, validation and collection scenarios.

## Design
[`ParkingApp`](../src/parking/app/ParkingApp.java) is an interactive menu application. [`ParkingService`](../src/parking/service/ParkingService.java) coordinates a `ParkingFacility`, a `PricingRule`, active registrations and daily totals. Compile all source files and launch `parking.app.ParkingApp`; choose `0` to quit. The deterministic automated test program is [`SystemTest`](../src/parking/app/SystemTest.java).

The current implementation uses first-fit allocation, per-commenced-hour car/two-wheeler rates, a zero-minute exemption, pass coverage, duplicate checks and validated coordinates/type names.

## Test results
The following values are the actual output captured from running `SystemTest` with Java 17:

| Test case | Expected output | Actual output | Status |
|---|---|---|---|
| Two-wheeler entry and exit | fee=30.0; available=2 | fee=30.0; available=2 | PASS |
| Car entry and exit | fee=90.0; available=1 | fee=90.0; available=1 | PASS |
| Electric-car entry and exit | fee=210.0; available=1 | fee=210.0; available=1 | PASS |
| Last available slot | ticket issued; available=0 | ticket issued; available=0 | PASS |
| No available slot | No parking slot is available. | No parking slot is available. | PASS |
| Zero-minute parking | 0.0 | 0.0 | PASS |
| Fee slab boundary (60 / 61 minutes) | 90.0 / 180.0 | 90.0 / 180.0 | PASS |
| Duplicate registration number | Vehicle is already parked: DUP01 | Vehicle is already parked: DUP01 | PASS |
| Invalid slot number | Invalid floor or slot number. | Invalid floor or slot number. | PASS |
| Invalid vehicle type | Invalid vehicle type: PLANE | Invalid vehicle type: PLANE | PASS |
| Monthly pass | 0.0 | 0.0 | PASS |
| Daily collection calculation | 120.0 | 120.0 | PASS |

Full console output is saved in [`system-tests.txt`](../outputs/system-tests.txt).

## Alternative concept
An embedded in-memory database or persistent file store could preserve active tickets across restarts. It introduces schema, transaction and recovery concerns; maps are sufficient for this assignment's single-process run.

## Complexity
Entry allocation is O(S) worst-case plus O(1) average duplicate lookup. Exit currently scans active tickets by ticket ID, O(A), then updates the daily `HashMap` in O(1) average. Storage is O(S + A + D).

## Result
All 12 requested test scenarios pass in the executable test run.
