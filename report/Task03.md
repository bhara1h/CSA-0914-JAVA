# Task 03 - Control Logic and Jagged Parking Facility

## Aim
Allocate and release the first available slot using a jagged array.

## Design / pseudocode

```text
ALLOCATE:
  for each floor, from first to last
    for each slot on that floor, from first to last
      if slot is free: mark occupied and return (floor, slot)
  return null

RELEASE(floor, slot):
  validate both indexes
  mark that slot free

CALCULATE_FEE(vehicle, entry, exit):
  reject invalid times
  minutes = whole minutes between entry and exit
  return vehicle-specific pricing rule for minutes and entry time
```

The production implementation uses `ParkingSlot[][]` in [`ParkingFacility`](../src/parking/service/ParkingFacility.java). The compact program [`Task03Demo`](../src/parking/app/Task03Demo.java) is the supplied complete-code style example and retains the required output exactly.

## Program output

```text
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```

## Alternative concept
A single flat Boolean array plus a floor-offset table could store availability. It has simple contiguous storage, but translating indexes and supporting irregular floor sizes requires bookkeeping. The chosen jagged array follows the floor/slot model directly and supports different capacities.

## Complexity
Allocation is O(S) in the worst case and O(1) in the best case; releasing a known slot is O(1). The jagged facility takes O(S) space. The standalone demo's allocation scans O(S) Boolean cells.

## Result
The demo output matches the required six lines, and the production facility supports any non-empty list of floor capacities.
