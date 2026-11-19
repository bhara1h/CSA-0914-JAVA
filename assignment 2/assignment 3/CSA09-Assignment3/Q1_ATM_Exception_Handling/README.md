# Q1: Bank ATM Exception Handling

## Files
- `ATM.java`: interactive ATM program
- `ATMTest.java`: command-line test cases
- `account.txt`: sample account balances
- `ExceptionHierarchy.md`: exception and catch documentation
- `TestCases.csv`: test table

## Compile

```powershell
cd "CSA09-Assignment3\Q1_ATM_Exception_Handling"
javac *.java
```

## Run

```powershell
java ATM
java ATMTest
```

Run `java ATM` from this folder because `account.txt` is read from the current working directory.

## Code Notes

- `IOException` is checked and covers file read/write failures.
- `IllegalArgumentException` and `NumberFormatException` are unchecked.
- `InsufficientBalanceException` is a checked custom exception.
- `finally` always reports that the ATM attempt finished.
- `withdrawWithErrorCode` demonstrates an exception-free alternative.
