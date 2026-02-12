# Power of Two (LeetCode 231)

## Problem Statement

Given an integer `n`, return `true` if it is a power of two. Otherwise, return `false`.

An integer `n` is a power of two if there exists an integer `x` such that `n == 2^x`.

**Example 1:**
```
Input: n = 1    → Output: true   (2^0 = 1)
Input: n = 16   → Output: true   (2^4 = 16)
Input: n = 3    → Output: false
Input: n = 0    → Output: false
Input: n = -1   → Output: false
```

**Constraints:**
- `-2^31 <= n <= 2^31 - 1`

---

## Why Bit Manipulation Matters

Bit manipulation problems test your understanding of how numbers are represented in binary. A power of two has exactly one bit set in its binary representation:

```
1   = 00000001  (2^0)
2   = 00000010  (2^1)
4   = 00000100  (2^2)
8   = 00001000  (2^3)
16  = 00010000  (2^4)
32  = 00100000  (2^5)
64  = 01000000  (2^6)
128 = 10000000  (2^7)
```

Non-powers-of-two have more than one bit set:
```
3  = 00000011  (two bits)
6  = 00000110  (two bits)
10 = 00001010  (two bits)
```

---

## Approach 1: n & (n - 1) Trick (Most Common)

**Time:** O(1), **Space:** O(1)

### Core Insight

The expression `n & (n - 1)` clears the lowest set bit of `n`. If `n` is a power of two, it has exactly one bit set. Clearing that single bit produces 0.

### How n & (n - 1) Works

```
n:       8 = 1000
n - 1:   7 = 0111
n & (n-1):   0000  → Result is 0 → Power of two ✓

n:      10 = 1010
n - 1:   9 = 1001
n & (n-1):   1000  → Result is NOT 0 → Not a power of two ✗

n:      16 = 10000
n - 1:  15 = 01111
n & (n-1):   00000  → Result is 0 → Power of two ✓

n:      12 = 1100
n - 1:  11 = 1011
n & (n-1):   1000  → Result is NOT 0 → Not a power of two ✗
```

### Why It Works

Subtracting 1 from a binary number flips all bits from the lowest set bit to the right (inclusive). ANDing with the original clears that lowest set bit. If there was only one bit set (power of two), the result is 0.

### Implementation

```java
public boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}
```

### Why `n > 0` Is Required

- `n = 0`: `0 & (-1) = 0`, which would incorrectly return true. But 0 is not a power of two.
- `n < 0`: Negative numbers in two's complement have the sign bit set and are never powers of two. For example, `n = -2147483648` (Integer.MIN_VALUE) has only one bit set in its binary representation, so `n & (n-1)` would be 0, but it is not a power of two.

---

## Approach 2: n & (-n) Trick

**Time:** O(1), **Space:** O(1)

### Core Insight

In two's complement, `-n` is the bitwise complement of `n` plus 1. The expression `n & (-n)` isolates (extracts) the lowest set bit. If `n` is a power of two, its lowest set bit IS the only bit, so `n & (-n) == n`.

```
n:     8  = 00001000
-n:   -8  = 11111000
n & (-n): = 00001000 = 8 = n → Power of two ✓

n:     12 = 00001100
-n:   -12 = 11110100
n & (-n): = 00000100 = 4 ≠ 12 → Not a power of two ✗
```

### Implementation

```java
public boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (-n)) == n;
}
```

---

## Approach 3: Integer.bitCount()

**Time:** O(1), **Space:** O(1)

### Implementation

```java
public boolean isPowerOfTwo(int n) {
    return n > 0 && Integer.bitCount(n) == 1;
}
```

`Integer.bitCount(n)` uses the Hamming weight algorithm to count the number of 1-bits. If exactly one bit is set, `n` is a power of two. This is the most readable approach.

---

## Approach 4: Loop / Divide

**Time:** O(log n), **Space:** O(1)

Repeatedly divide by 2. If the number becomes 1, it was a power of two. If it becomes odd at any point before reaching 1, it was not.

```java
public boolean isPowerOfTwo(int n) {
    if (n <= 0) return false;
    while (n > 1) {
        if (n % 2 != 0) return false;
        n /= 2;
    }
    return true;
}
```

This is correct but slower than the O(1) bit tricks. Mention it only if asked for a non-bit approach.

---

## Approach 5: Math (Logarithm)

**Time:** O(1), **Space:** O(1)

```java
public boolean isPowerOfTwo(int n) {
    if (n <= 0) return false;
    double logResult = Math.log(n) / Math.log(2);
    return Math.abs(logResult - Math.round(logResult)) < 1e-10;
}
```

**Warning**: Floating-point precision issues make this approach unreliable. For example, `Math.log(1 << 29) / Math.log(2)` might return `28.999999...` instead of `29.0`. Avoid this in interviews.

---

## Approach Comparison

| Approach | Time | Space | Correctness Risk | Interview Quality |
|----------|------|-------|-----------------|------------------|
| n & (n-1) | O(1) | O(1) | None | Best |
| n & (-n) | O(1) | O(1) | None | Great |
| bitCount | O(1) | O(1) | None | Good (uses API) |
| Loop/Divide | O(log n) | O(1) | None | Acceptable |
| Logarithm | O(1) | O(1) | Floating point | Avoid |

---

## Essential Bit Manipulation Tricks

The `n & (n - 1)` trick is foundational. Here is a complete reference of bit tricks every Lead Engineer should know:

| Trick | Expression | Purpose | Example |
|-------|-----------|---------|---------|
| Clear lowest set bit | `n & (n - 1)` | Remove rightmost 1-bit | `12 (1100) → 8 (1000)` |
| Isolate lowest set bit | `n & (-n)` | Get rightmost 1-bit | `12 (1100) → 4 (0100)` |
| Set bit at position k | `n \| (1 << k)` | Turn on bit k | `set bit 2 of 5: 0101 \| 0100 = 0101` |
| Clear bit at position k | `n & ~(1 << k)` | Turn off bit k | `clear bit 2 of 7: 0111 & 1011 = 0011` |
| Toggle bit at position k | `n ^ (1 << k)` | Flip bit k | `toggle bit 1 of 5: 0101 ^ 0010 = 0111` |
| Check bit at position k | `(n >> k) & 1` | Is bit k set? | `check bit 2 of 5: (0101 >> 2) & 1 = 1` |
| Check if even | `(n & 1) == 0` | LSB is 0 | `4 & 1 = 0 (even)` |
| Check if odd | `(n & 1) == 1` | LSB is 1 | `5 & 1 = 1 (odd)` |
| Multiply by 2^k | `n << k` | Left shift | `3 << 2 = 12` |
| Divide by 2^k | `n >> k` | Right shift (arithmetic) | `12 >> 2 = 3` |
| Swap without temp | `a ^= b; b ^= a; a ^= b;` | XOR swap | — |

---

## Follow-Up Problems

### Power of Four (LeetCode 342)

A number is a power of four if it is a power of two AND its single set bit is at an even position (0, 2, 4, 6, ...).

```java
public boolean isPowerOfFour(int n) {
    // 0x55555555 = 01010101...01 (bits set at even positions)
    return n > 0 && (n & (n - 1)) == 0 && (n & 0x55555555) != 0;
}
```

### Power of Three (LeetCode 326)

Cannot use bit tricks because 3 is not a power of 2. Use math instead:

```java
public boolean isPowerOfThree(int n) {
    // 3^19 = 1162261467 is the largest power of 3 that fits in int
    return n > 0 && 1162261467 % n == 0;
}
```

### Number of 1 Bits / Hamming Weight (LeetCode 191)

Count set bits using the `n & (n-1)` trick:

```java
public int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        n = n & (n - 1); // Clear lowest set bit
        count++;
    }
    return count;
}
```

### Counting Bits (LeetCode 338)

For every number from 0 to n, count the number of set bits:

```java
public int[] countBits(int n) {
    int[] result = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        result[i] = result[i & (i - 1)] + 1; // Key: i has one more bit than i&(i-1)
    }
    return result;
}
```

---

## Edge Cases

| Case | Input | Expected | Explanation |
|------|-------|----------|-------------|
| Zero | 0 | false | 0 is not a power of two |
| One | 1 | true | 2^0 = 1 |
| Negative | -1 | false | Negative numbers are not powers of two |
| INT_MIN | -2147483648 | false | Has one bit set but is negative |
| Large power | 1073741824 | true | 2^30 |

---

## LeetCode Similar Problems

- [191. Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/) — Hamming weight using n&(n-1)
- [326. Power of Three](https://leetcode.com/problems/power-of-three/) — Math approach
- [342. Power of Four](https://leetcode.com/problems/power-of-four/) — Bit mask approach
- [338. Counting Bits](https://leetcode.com/problems/counting-bits/) — DP with bit tricks
- [461. Hamming Distance](https://leetcode.com/problems/hamming-distance/) — XOR + count bits
- [136. Single Number](https://leetcode.com/problems/single-number/) — XOR trick

---

## Interview Tips

1. **Solve it in 30 seconds**: This is a warm-up problem. Know `n & (n - 1)` by heart.
2. **Explain the bit trick**: "A power of two has exactly one bit set. `n & (n-1)` clears the lowest set bit. If the result is 0, there was only one bit — hence power of two."
3. **Know the `n > 0` guard**: Explain why 0 and negative numbers need to be excluded.
4. **Be ready for follow-ups**: Power of Four (bit mask), Power of Three (math), Number of 1 Bits (counting).
5. **Master the bit tricks table**: The tricks listed above appear in many problems. Knowing them cold saves time in interviews.
