# Task 10 - Encapsulation and Packages

## Aim
Organize source by responsibility and protect object state.

## Package structure

```text
parking.model       Vehicle, subclasses, slot, ticket and passes
parking.service     Facility, parking operations and vehicle factory
parking.app         CLI, task demonstrations and test programs
parking.interfaces  Chargeable and ParkingPass contracts
parking.pricing     PricingRule and standard pricing implementation
```

## Explanation
Fields are private; state is read through getters. Registration, owner, pass date, slot coordinates, time intervals and constructor arguments are validated where they enter the model. Slot assignment and release are explicit methods, so callers cannot directly corrupt occupancy. Public classes and methods form the application API; internal fields remain private.

Primitive `int` and reference wrapper `Integer` represent the same numeric concept differently. Collections such as `ArrayList<Integer>` require objects, so Java automatically boxes an `int` to `Integer` and unboxes it on use. A wrapper can be `null`; unboxing null throws `NullPointerException`. [`WrapperDemo`](../src/parking/app/WrapperDemo.java) demonstrates autoboxing and unboxing.

## Alternative concept
Public fields would reduce getter/setter code but allow invalid values at any call site. A builder can improve construction of objects with many optional parameters, but is unnecessary for these small beginner-level models.

## Complexity
Field access/validation is O(1) (excluding string length); object state occupies O(1) references plus the stored strings. Package organization does not change runtime complexity.

## Result
All required packages are present, mutable slot state is encapsulated, and inputs are checked before use.
