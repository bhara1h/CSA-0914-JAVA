# Q1 Exception Hierarchy

## Throwable Hierarchy

```text
Throwable
├── Error
│   └── Serious JVM failures such (OutOfMemoryError)
└── Exception
    ├── IOException (checked)
    │   └── FileNotFoundException
    ├── RuntimeException (unchecked)
    │   ├── IllegalArgumentException
    │   │   └── NumberFormatException
    │   └── InputMismatchException
    └── InsufficientBalanceException (custom checked exception)
```

## Classification

| Exception | Checked/Unchecked | Cause in this program | Catch order |
|---|---|---|---|
| IOException | Checked | Missing/unreadable account file | Specific catch |
| NumberFormatException | Unchecked | Invalid numeric text in file | Specific catch |
| IllegalArgumentException | Unchecked | Negative withdrawal amount | Specific catch |
| InputMismatchException | Unchecked | Non-numeric console input | Specific catch |
| InsufficientBalanceException | Checked | Withdrawal exceeds balance | Specific catch |

## Catch-Block Execution

The catches are specific and ordered before no broader catch. `IllegalArgumentException` is caught before `Exception`; `InsufficientBalanceException` and `IOException` are handled separately. `finally` always runs after normal execution or a caught exception.

Example:

```text
java ATM
Enter account number: 12345
Enter withdrawal amount: -100
Error: Withdrawal amount cannot be negative.
Finally: ATM attempt completed.
```

## Stack Trace Example

An uncaught `NumberFormatException` could produce a trace beginning:

```text
java.lang.NumberFormatException: For input string: "abc"
    at java.lang.NumberFormatException.forInputString(...)
    at java.lang.Integer.parseInt(...)
```

The project catches this exception, so normal ATM execution prints a meaningful message.

## Broad Catch Ordering

This is wrong:

```java
catch (Exception e) {
    // broad
} catch (IOException e) {
    // unreachable
}
```

Java rejects unreachable subclasses. If it were placed first, it would hide the specific messages for file, balance, and input errors. Catch the most specific classes first.

## Alternative Using hasNextInt and Error Codes

`withdrawWithErrorCode(Scanner)` uses `hasNextInt()`. Suggested codes:

- 200: success
- 301: missing account number
- 302: invalid amount text
- 303: negative amount
- 304: insufficient balance
- 305: account not found
- 306: file/error

Code is predictable and avoids stack creation. However, exceptions usually have better readability and preserve more context. Exceptions are more expensive for exceptional paths because stack information is created. For normal, valid input, both approaches can be fast; exceptions are not appropriate for routinely expected invalid input.
