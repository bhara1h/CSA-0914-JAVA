# Task 02 - Data Representation

## Aim
Select safe Java representations for vehicles, floors, time and user input.

## Explanation
The program uses `int` for floor/slot indexes and minute counts, `long` for durations and ticket sequence numbers, `double` for sample fees, `String` for registrations, and `LocalDate` / `LocalDateTime` for calendar dates and timestamps. A jagged `ParkingSlot[][]` represents floors with different numbers of slots; for example, `{4, 3, 5, 2, 4}` represents five floors and 18 slots. A slot object represents availability through a `null` vehicle reference.

The CLI reads complete lines with `Scanner.nextLine()`, then parses numeric/date values. Parsing can throw `NumberFormatException` or `DateTimeParseException`; validation should report bad input rather than silently turn it into zero. Widening `int` to `long` is safe; narrowing a large `long` to `int` may overflow. A cast does not validate user data.

## Design and alternatives
The selected jagged array is direct and matches uneven floors. A one-dimensional array would require an index-to-floor conversion table (or fixed floor sizes); it is simple if all slots are interchangeable. An `ArrayList<ParkingSlot[]>` could grow the floor list dynamically, but adds collection operations and does not itself enforce the array dimensions. An `ArrayList<ParkingSlot>` is also a reasonable flat-slot alternative.

## Code and output
The actual representation is in [`ParkingFacility`](../src/parking/service/ParkingFacility.java). Wrapper conversion is demonstrated by [`WrapperDemo`](../src/parking/app/WrapperDemo.java). Run it with:

```text
java -cp build parking.app.WrapperDemo
```

```text
Primitive int: 5
Wrapper Integer: 5
Unboxed int: 5
ArrayList-compatible value: 5
```

## Complexity
Creating the floor and slot arrays is O(S) time and O(S) space for S slots. Accessing a slot by floor and index is O(1). A full occupancy scan is O(S).

## Result
The jagged array is appropriate for the required uneven floor capacities, with explicit input validation and no unsafe narrowing conversion.
