# Task 07 - Method Overriding

## Aim
Provide subtype-specific fee calculations and reliable object representations.

## Explanation
`Vehicle.calculateFee(long)` is abstract and each concrete vehicle overrides it. The compiler-checked `@Override` annotation catches signature mistakes. `toString()` presents a useful vehicle description; `equals()` and `hashCode()` use the concrete vehicle class and normalized registration number. This keeps equal vehicles consistent with hash-based collections. Tickets compare by unique ticket number.

`final` prevents a class from being subclassed, a method from being overridden, or a variable from being reassigned. It should be used only where that restriction is intended; this extensible vehicle design does not mark its fee method final.

## Code and output
See [`Vehicle`](../src/parking/model/Vehicle.java), its subtype overrides, and [`OverrideDemo`](../src/parking/app/OverrideDemo.java).

```text
Car TN22AB1234
true
true
90.0
```

## Alternative concept
A switch statement in the service could choose a fee based on a vehicle-type string. It is straightforward for a few cases, but each new type requires modifying centralized logic and errors can occur from misspelled strings. Overriding keeps the behavior with its subtype.

## Complexity
Fee calculation is O(1) for the standard vehicle types; equality and hashing are O(R) for registration-string length R. Object and string storage is O(R).

## Result
Vehicle-specific behavior is dispatched safely at runtime, and equality/hash semantics agree.
