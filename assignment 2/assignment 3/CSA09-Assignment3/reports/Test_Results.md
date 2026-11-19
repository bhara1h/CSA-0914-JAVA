# Test Results

All listed Q1 automated tests were executed with Java 17.0.12.

| Test Case | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|
| Valid withdrawal | 12345, 500 | Remaining 4500.00 | 4500.00 | PASS |
| Exact balance | 67890, 1000 | Remaining 0.00 | 0.00 | PASS |
| Zero withdrawal | 12345, 0 | Remaining 4500.00 | 4500.00 | PASS |
| Negative amount | 12345, -100 | Negative amount error | Negative amount error | PASS |
| Insufficient balance | 67890, 1001 | Insufficient balance error | Insufficient balance error | PASS |
| Missing file | Missing temp path | File I/O error | IOException caught | PASS |
| Unknown account | 99999, 100 | Account not found | IOException caught | PASS |
| Invalid balance | Invalid numeric text | Invalid numeric balance error | NumberFormatException caught | PASS |
| Invalid text | abc | Invalid text input error | hasNextDouble() rejected abc | PASS |
