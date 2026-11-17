# Task 01 - Understand the Problem

## Aim
Analyse the campus parking problem before choosing classes or data structures.

## Explanation and requirements
The system accepts vehicle type, registration number, owner category, entry/exit time and optional pass details. It returns an entry ticket and slot, exit fee, available-slot count and daily collection. Candidate classes are `Vehicle` and its vehicle subtypes, `ParkingTicket`, `ParkingSlot`, `ParkingFacility`, `ParkingService`, pricing rules and parking passes.

Responsibilities: a vehicle owns its identity and fee behaviour; a slot owns its occupancy; a facility owns the floors; a ticket records a stay; the service coordinates entry, exit, duplicate prevention and collection; pricing calculates fees; a pass represents fee coverage.

Business rules used here:
- Registration numbers are required and normalized to uppercase. A vehicle cannot hold two active tickets.
- Allocation uses the first free slot in floor/slot order; an unavailable facility rejects entry.
- Parking bills each commenced hour; zero minutes cost zero. Rates are Rs 30/hour for a two-wheeler, Rs 90/hour for a car, and Rs 90/hour plus Rs 120 charging fee for an electric car.
- E-bikes cost a flat Rs 25 for a non-zero stay. Delivery vans cost the car rate, doubled for each billed hour beginning between 09:00 and 16:59.
- Valid monthly and one-day passes cover parking fees. Collection is grouped by exit date.

Assumptions: Indian rupees are represented as `double` for this educational exercise; production billing should use `BigDecimal`. EV charging is a fixed per-session fee. Rates and five-floor slot counts are examples and can be changed in the constructor.

## Design and alternatives
The implementation stores vehicle objects in slots and uses maps for active tickets and daily collections. An alternative is parallel arrays for registration, type, slot and time. Arrays use compact primitive storage, but their fields must stay synchronized and adding vehicle-specific behaviour becomes cumbersome. Objects keep related data and methods together and make inheritance/polymorphism straightforward.

## Complexity
For `S` total slots and `A` active vehicles, first-fit allocation is O(S), active-registry lookup/insertion is O(1) average, and ticket-number lookup on exit is O(A). The facility and active registry use O(S + A) space; collection history uses O(D), where D is the number of dates with exits.

## Result
Problem responsibilities, assumptions and a suitable object-based solution are established.
