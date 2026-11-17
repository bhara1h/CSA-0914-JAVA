# Task 09 - Abstraction

## Aim
Expose common vehicle operations while requiring concrete fee behaviour.

## Explanation
`Vehicle` is abstract because a generic vehicle has no single correct rate. It stores common state and declares abstract `calculateFee`. `Chargeable` is an interface implemented by `ElectricCar` and `EBike`; callers can request charging behaviour without depending on the concrete class.

[`AbstractionDemo`](../src/parking/app/AbstractionDemo.java) creates a `Vehicle[]`, then invokes each object's override. It uses `instanceof` to access the optional charging contract for chargeable vehicles.

## Output excerpt

```text
TwoWheeler TN01AA -> Rs 30.0
Car TN02BB -> Rs 90.0
ElectricCar TN03CC -> Rs 210.0
Charging fee -> Rs 120.0
```

## Alternative concept
A concrete base class could return a default rate, but a missing subtype override would silently apply the wrong price. The abstract method forces every concrete subtype to define its behavior. A chargeable abstract base class would unnecessarily restrict multiple inheritance of contracts; the interface can be implemented independently.

## Complexity
Virtual dispatch is O(1) for fee selection; EV charging calculations are O(1). A collection of N vehicles uses O(N) space and printing it takes O(N) time.

## Result
The abstract base type and independent chargeable contract are both implemented and exercised.
