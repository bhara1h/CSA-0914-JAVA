# Task 04 - Classes, Objects and Constructors

## Aim
Model the parking domain with classes and demonstrate constructor forms and chaining.

## Explanation and design
`Vehicle` contains validated registration and owner fields. `ParkingTicket` links a vehicle, slot, entry time and optional pass. `ParkingSlot` stores its location and current assignment. `ParkingFacility` creates its jagged floors; `ParkingService` coordinates transactions.

The `TwoWheeler` class demonstrates a default constructor, one-argument constructor, and full parameterized constructor. The short constructors use `this(...)` to chain to the full constructor. That full constructor calls `super(...)`, so the base `Vehicle` constructor executes before the subclass constructor body. `ParkingFacility()` similarly delegates to its capacity-array constructor.

Relevant source: [`Vehicle`](../src/parking/model/Vehicle.java), [`TwoWheeler`](../src/parking/model/TwoWheeler.java), [`ParkingTicket`](../src/parking/model/ParkingTicket.java), and [`ConstructorDemo`](../src/parking/app/ConstructorDemo.java).

## Program output

```text
Default constructor: TwoWheeler UNKNOWN
One-argument constructor: TwoWheeler TN10X1, owner=VISITOR
Parameterized constructor: TwoWheeler TN11Y2, owner=STAFF
Constructor order: Vehicle constructor runs before TwoWheeler body.
```

## Alternative concept
One large `ParkingSystem` class could contain vehicle fields and slot logic. It would reduce the number of files but mix responsibilities, complicate testing and make new vehicle kinds harder to add. Small domain classes have clearer ownership and reuse.

## Complexity
Constructing the facility is O(S); constructing an individual vehicle, ticket or slot is O(1). The object graph occupies O(S + A) space for S slots and A active tickets.

## Result
The model has explicit constructors, validated state, constructor chaining, and base-class initialization.
