# Integer to Roman

## Problem Statement
Convert an integer to its Roman numeral representation. Roman numerals use symbols: I=1, V=5, X=10, L=50, C=100, D=500, M=1000.

## Example
```
Input: num = 1994
Output: "MCMXCIV"
Explanation: M = 1000, CM = 900, XC = 90, IV = 4
```

## Roman Numeral Rules:
1. **Symbols written largest to smallest (left to right)**
2. **Subtractive cases:** IV (4), IX (9), XL (40), XC (90), CD (400), CM (900)
3. **No more than 3 consecutive identical symbols**

## Approach: Greedy with Value-Symbol Mapping

### How it works:
1. **Create mapping** of values to Roman symbols (including subtractive cases)
2. **Process from largest to smallest value**
3. **Use as many symbols as possible** for each value

### Key Logic:
```java
int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

StringBuilder result = new StringBuilder();
for (int i = 0; i < values.length; i++) {
    while (num >= values[i]) {
        result.append(symbols[i]);
        num -= values[i];
    }
}
```

### Time & Space Complexity:
- **Time:** O(1) - At most 13 iterations
- **Space:** O(1) - Fixed size arrays and result

## Alternative Approach: Hardcoded Ranges
- **Thousands:** M, MM, MMM
- **Hundreds:** "", C, CC, CCC, CD, D, DC, DCC, DCCC, CM
- **Tens:** "", X, XX, XXX, XL, L, LX, LXX, LXXX, XC
- **Units:** "", I, II, III, IV, V, VI, VII, VIII, IX

## LeetCode Similar Problems:
- [13. Roman to Integer](https://leetcode.com/problems/roman-to-integer/)
- [171. Excel Sheet Column Number](https://leetcode.com/problems/excel-sheet-column-number/)
- [168. Excel Sheet Column Title](https://leetcode.com/problems/excel-sheet-column-title/)
- [273. Integer to English Words](https://leetcode.com/problems/integer-to-english-words/)

## Interview Tips:
- Remember all subtractive cases (IV, IX, XL, XC, CD, CM)
- Use greedy approach - always take largest possible value
- Consider both approaches: mapping vs hardcoded ranges
- Handle edge case: num = 0 (though not in Roman system) 