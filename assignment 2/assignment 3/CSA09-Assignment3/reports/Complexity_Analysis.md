# Complexity Analysis

## Q1

| Operation | Time Complexity | Space Complexity |
|---|---|---|
| Read account file | O(n) | O(n) for stored lines |
| Search for account | O(n) | O(1) additional |
| Update account file | O(n) | O(n) for rebuilt contents |

- Best case: account is found near the beginning, approximately O(1) search time after file loading.
- Worst case: account is missing or is near the end, O(n).
- Exception alternative: readable and richer, but exception creation costs stack-trace work.
- Error-code alternative: no exception stack-trace cost, but less descriptive context.
