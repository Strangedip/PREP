# Integer to Roman (LeetCode 12)

## Problem Statement

Roman numerals are represented by seven symbols: I, V, X, L, C, D, and M.

| Symbol | Value |
|--------|-------|
| I | 1 |
| V | 5 |
| X | 10 |
| L | 50 |
| C | 100 |
| D | 500 |
| M | 1000 |

Roman numerals are usually written largest to smallest from left to right. However, there are six instances where subtraction is used:
- `I` can be placed before `V` (5) and `X` (10) to make 4 and 9.
- `X` can be placed before `L` (50) and `C` (100) to make 40 and 90.
- `C` can be placed before `D` (500) and `M` (1000) to make 400 and 900.

Given an integer, convert it to a Roman numeral.

**Example 1:**
```
Input: num = 3749
Output: "MMMDCCXLIX"
Explanation: 3000 = MMM, 700 = DCC, 40 = XL, 9 = IX
```

**Example 2:**
```
Input: num = 58
Output: "LVIII"
Explanation: 50 = L, 5 = V, 3 = III
```

**Example 3:**
```
Input: num = 1994
Output: "MCMXCIV"
Explanation: 1000 = M, 900 = CM, 90 = XC, 4 = IV
```

**Constraints:**
- `1 <= num <= 3999`

---

## Approach 1: Greedy with Value-Symbol Mapping (Optimal)

**Time:** O(1) — bounded by constant number of iterations (at most 15 symbols for 3999)
**Space:** O(1) — result string is bounded

### How It Works

The key insight is that Roman numerals are a greedy representation. You always use the largest possible symbol that fits into the remaining number. By including the six subtractive forms (4, 9, 40, 90, 400, 900) in our value table alongside the seven standard symbols, we get a complete set of 13 entries that we process greedily from largest to smallest.

### Complete Implementation

```java
public class IntegerToRoman {
    
    public String intToRoman(int num) {
        // Values in descending order, including subtractive forms
        int[] values =    {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < values.length; i++) {
            // Use as many of this symbol as possible
            while (num >= values[i]) {
                result.append(symbols[i]);
                num -= values[i];
            }
        }
        
        return result.toString();
    }
}
```

### Dry Run Example

```
Input: num = 1994

Iteration 1 (value=1000, symbol="M"):
  1994 >= 1000 → append "M", num = 994
  994 < 1000 → stop
  Result so far: "M"

Iteration 2 (value=900, symbol="CM"):
  994 >= 900 → append "CM", num = 94
  94 < 900 → stop
  Result so far: "MCM"

Iterations 3-6 (500, 400, 100, 90):
  94 < 500 → skip
  94 < 400 → skip
  94 < 100 → skip
  94 >= 90 → append "XC", num = 4
  Result so far: "MCMXC"

Iterations 7-11 (50, 40, 10, 9, 5):
  4 < 50 → skip
  4 < 40 → skip
  4 < 10 → skip
  4 < 9 → skip
  4 < 5 → skip

Iteration 12 (value=4, symbol="IV"):
  4 >= 4 → append "IV", num = 0
  Result so far: "MCMXCIV"

Final result: "MCMXCIV"
```

### Why Greedy Works Here

Roman numerals are designed so that the greedy approach always produces the correct and canonical representation. Each value in our table is at least twice the next value (except for the subtractive pairs), which guarantees no alternative decomposition produces a shorter or different valid Roman numeral.

---

## Approach 2: Hardcoded Lookup Table

**Time:** O(1), **Space:** O(1)

### How It Works

Since the input is constrained to 1-3999, we can create separate lookup tables for thousands, hundreds, tens, and units. Extract each digit and look up its Roman representation directly.

```java
public String intToRoman(int num) {
    String[] thousands = {"", "M", "MM", "MMM"};
    String[] hundreds  = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    String[] tens      = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    String[] units     = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    
    return thousands[num / 1000] + 
           hundreds[(num % 1000) / 100] + 
           tens[(num % 100) / 10] + 
           units[num % 10];
}
```

### Dry Run Example

```
Input: num = 1994

thousands[1994 / 1000] = thousands[1] = "M"
hundreds[(1994 % 1000) / 100] = hundreds[994 / 100] = hundreds[9] = "CM"
tens[(1994 % 100) / 10] = tens[94 / 10] = tens[9] = "XC"
units[1994 % 10] = units[4] = "IV"

Result: "M" + "CM" + "XC" + "IV" = "MCMXCIV"
```

### When to Use This Approach

This approach is extremely fast (no loops at all, just array lookups and arithmetic) and very easy to verify correctness. It is a valid interview answer, but the greedy approach demonstrates more algorithmic thinking.

---

## Approach Comparison

| Approach | Time | Space | Lines of Code | Algorithmic Depth | Extensibility |
|----------|------|-------|--------------|-------------------|---------------|
| Greedy with Mapping | O(1) | O(1) | ~15 | High (greedy pattern) | Easy to extend to larger values |
| Hardcoded Lookup | O(1) | O(1) | ~10 | Low (brute enumeration) | Hard to extend beyond 3999 |

---

## The Reverse Problem: Roman to Integer (LeetCode 13)

This is the inverse problem and a very common companion question. The key insight is: if a smaller value appears before a larger value, subtract it; otherwise, add it.

```java
public int romanToInt(String s) {
    Map<Character, Integer> map = Map.of(
        'I', 1, 'V', 5, 'X', 10, 'L', 50,
        'C', 100, 'D', 500, 'M', 1000
    );
    
    int result = 0;
    for (int i = 0; i < s.length(); i++) {
        int current = map.get(s.charAt(i));
        int next = (i + 1 < s.length()) ? map.get(s.charAt(i + 1)) : 0;
        
        if (current < next) {
            result -= current;  // Subtractive case (e.g., I before V = 4)
        } else {
            result += current;
        }
    }
    
    return result;
}
```

**Dry Run for "MCMXCIV" → 1994:**
```
M(1000): next=C(100), 1000 > 100 → add 1000.  Total: 1000
C(100):  next=M(1000), 100 < 1000 → subtract 100.  Total: 900
M(1000): next=X(10), 1000 > 10 → add 1000.  Total: 1900
X(10):   next=C(100), 10 < 100 → subtract 10.  Total: 1890
C(100):  next=I(1), 100 > 1 → add 100.  Total: 1990
I(1):    next=V(5), 1 < 5 → subtract 1.  Total: 1989
V(5):    no next → add 5.  Total: 1994
```

---

## Common Roman Numeral Rules to Memorize

| Rule | Description | Example |
|------|-------------|---------|
| **Additive** | Larger or equal values left to right are added | VI = 5 + 1 = 6 |
| **Subtractive** | Smaller value before larger is subtracted | IV = 5 - 1 = 4 |
| **Max 3 consecutive** | No more than 3 of the same symbol in a row | III = 3 (IIII is invalid) |
| **Only certain subtractions** | I before V/X; X before L/C; C before D/M | IL (49) is NOT valid; XLIX is correct |

---

## Edge Cases

| Case | Input | Expected Output |
|------|-------|----------------|
| Minimum value | 1 | "I" |
| Maximum value | 3999 | "MMMCMXCIX" |
| All subtractive | 1994 | "MCMXCIV" |
| No subtractive | 2023 | "MMXXIII" |
| Single digit | 4 | "IV" |
| Round number | 1000 | "M" |

---

## LeetCode Similar Problems

- [13. Roman to Integer](https://leetcode.com/problems/roman-to-integer/) — The reverse conversion
- [171. Excel Sheet Column Number](https://leetcode.com/problems/excel-sheet-column-number/) — Base conversion
- [168. Excel Sheet Column Title](https://leetcode.com/problems/excel-sheet-column-title/) — Reverse base conversion
- [273. Integer to English Words](https://leetcode.com/problems/integer-to-english-words/) — Number to words conversion
- [1689. Partitioning Into Minimum Number Of Deci-Binary Numbers](https://leetcode.com/problems/partitioning-into-minimum-number-of-deci-binary-numbers/)

---

## Interview Tips

1. **Know all 13 values by heart**: The seven standard symbols plus the six subtractive forms (4, 9, 40, 90, 400, 900).
2. **Explain the greedy strategy**: "I process values from largest to smallest, using each as many times as possible."
3. **Mention both approaches**: Show the greedy approach first (demonstrates algorithm design), then mention the lookup table as an alternative (demonstrates practical thinking).
4. **Be ready for the reverse**: Roman to Integer is almost always asked as a pair with Integer to Roman.
5. **Discuss constraints**: The problem is limited to 1-3999 because the standard Roman system does not have a symbol for 5000. Mention that extended Roman numerals use bars (vinculum) for larger values.
6. **Handle the "why subtractive" question**: Subtractive notation was introduced to avoid four consecutive identical symbols (IIII → IV), making numbers shorter and more readable.
