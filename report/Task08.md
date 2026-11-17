# Task 08 - Polymorphism

## Aim
Demonstrate compile-time overloading and runtime overriding.

## Explanation
The overloaded `calculateFee(int hours, int extraMinutes)` and `calculateFee(long parkedMinutes)` are selected at compile time. In [`FeeDemo`](../src/parking/app/FeeDemo.java), variables have the base type `Vehicle`, but the runtime object selects the appropriate overridden fee implementation. `equals` also dispatches through the base reference.

## Program output
The required output is preserved exactly:

```text
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```

Run using `java -cp build parking.app.FeeDemo`.

## Alternative concept
An explicit `if/else` type test can calculate fees, but it couples callers to every vehicle subtype. A separate pricing-rule object is another valid runtime-polymorphism design and is used by the full service to separate pricing policy from orchestration.

## Complexity
Overload selection and method dispatch are O(1); the fee calculation is O(1), except delivery van time-window billing at O(H). Space is O(1) per call.

## Result
The complete-code demo produces all four expected lines verbatim.
