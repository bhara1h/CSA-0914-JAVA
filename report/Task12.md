# Task 12 - Design Challenge

## Aim
Extend the system for an e-bike, delivery van, one-day visitor pass and five uneven floors.

## Design
The shared facility already stores `ParkingSlot[][]` and accepts different slot counts. The default facility now has five floors with capacities `{4, 3, 5, 2, 4}`. `EBike` overrides fee behavior with a flat Rs 25 per non-zero stay and implements `Chargeable`. `DeliveryVan` overrides fee calculation and applies twice the car rate to each billed hour beginning from 09:00 through 16:59. `OneDayVisitorPass` covers fees on its configured date. `PricingRule` separates the system's pricing policy from parking coordination.

The existing vehicle/service APIs are extended through subtypes and the pass abstraction rather than separate duplicate parking systems. Tests are in [`DesignChallengeTest`](../src/parking/app/DesignChallengeTest.java).

## Test results

| Requirement | Expected output | Actual output | Status |
|---|---|---|---|
| E-bike flat fee | 25.0 | 25.0 | PASS |
| Delivery van at 10 AM for one billed hour | 180.0 | 180.0 | PASS |
| Valid one-day visitor pass | 0.0 | 0.0 | PASS |
| Five floors with uneven slot counts | 5 floors / 9 slots | 5 floors / 9 slots | PASS |

The actual captured console output is in [`design-challenge-tests.txt`](../outputs/design-challenge-tests.txt).

## Alternative concept
Rates could be held in a configurable map or read from a database instead of subclass overrides. A table-driven approach makes rates easier to configure but may weaken the object-oriented subtype demonstration. For a changing production tariff, a configurable `PricingRule` strategy is preferable.

## Complexity
E-bike and visitor pass fee evaluation is O(1). Van pricing is O(H) for H billed hours. Facility allocation is O(S); floor/slot storage is O(S), with five floors independent of their individual sizes.

## Result
All four design-challenge behaviors are implemented and pass the new requirement checks.
