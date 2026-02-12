# Counting Bits

## Problem Statement

Given an integer `n`, return an array `ans` of length `n + 1` such that for each `i` (0 ≤ i ≤ n), `ans[i]` is the **number of 1's** in the binary representation of `i`.

**Constraints:**
- 0 ≤ n ≤ 10^5

## Example
```
Input: n = 5
Output: [0, 1, 1, 2, 1, 2]
Explanation:
  0 → 0000 → 0 ones
  1 → 0001 → 1 one
  2 → 0010 → 1 one
  3 → 0011 → 2 ones
  4 → 0100 → 1 one
  5 → 0101 → 2 ones
```

## Approach 1: Brute Force (Count bits for each number)

```java
public int[] countBitsBrute(int n) {
    int[] result = new int[n + 1];
    for (int i = 0; i <= n; i++) {
        result[i] = Integer.bitCount(i);  // Or implement countBits manually
    }
    return result;
}

// Manual bit counting
private int popcount(int x) {
    int count = 0;
    while (x != 0) {
        count += (x & 1);  // Check last bit
        x >>= 1;           // Shift right
    }
    return count;
}
```

**Time**: O(n × log n) — for each number, count up to log(n) bits.
**Space**: O(1) extra.

## Approach 2: DP with Last Set Bit (n & (n-1))

### Key Insight
`n & (n - 1)` removes the lowest set bit from `n`. So `countBits(n) = countBits(n & (n-1)) + 1`.

```java
public int[] countBits(int n) {
    int[] dp = new int[n + 1];
    dp[0] = 0;
    
    for (int i = 1; i <= n; i++) {
        dp[i] = dp[i & (i - 1)] + 1;  // Remove lowest set bit, add 1
    }
    
    return dp;
}
```

### Why `i & (i - 1)` Works
```
Example: i = 12 (1100)
  i - 1 = 11 (1011)
  i & (i-1) = 8 (1000) → removed the lowest set bit

So: countBits(12) = countBits(8) + 1
                   = 1 + 1 = 2 ✓ (1100 has 2 ones)
```

**Time**: O(n) — single pass, O(1) per element.
**Space**: O(1) extra.

## Approach 3: DP with Right Shift

### Key Insight
`countBits(n) = countBits(n >> 1) + (n & 1)`. Shifting right divides by 2, and we add 1 if the last bit is set.

```java
public int[] countBitsShift(int n) {
    int[] dp = new int[n + 1];
    
    for (int i = 1; i <= n; i++) {
        dp[i] = dp[i >> 1] + (i & 1);
    }
    
    return dp;
}
```

### Why It Works
```
countBits(5) = countBits(5 >> 1) + (5 & 1)
             = countBits(2) + 1
             = 1 + 1 = 2 ✓

5 = 101, 2 = 10 (same bits except the last one)
```

**Time**: O(n)
**Space**: O(1) extra.

## Approach 4: DP with Most Significant Bit

### Key Insight
If `i` has the most significant bit at position `p`, then `countBits(i) = 1 + countBits(i - 2^p)`.

```java
public int[] countBitsMSB(int n) {
    int[] dp = new int[n + 1];
    int msb = 1;  // Most significant bit power
    
    for (int i = 1; i <= n; i++) {
        if (msb * 2 == i) {
            msb = i;  // Update MSB when we reach the next power of 2
        }
        dp[i] = 1 + dp[i - msb];
    }
    
    return dp;
}
```

**Time**: O(n)
**Space**: O(1) extra.

## Comparison

| Approach | Time | Space | Key Formula |
|----------|------|-------|-------------|
| Brute Force | O(n log n) | O(1) | Manual popcount per number |
| **DP + Last Set Bit** | **O(n)** | **O(1)** | `dp[i] = dp[i & (i-1)] + 1` |
| DP + Right Shift | O(n) | O(1) | `dp[i] = dp[i >> 1] + (i & 1)` |
| DP + MSB | O(n) | O(1) | `dp[i] = 1 + dp[i - msb]` |

All three DP approaches are O(n). The "last set bit" approach (`i & (i-1)`) is the most elegant.

## Essential Bit Manipulation Tricks

These are must-know operations for any bit manipulation interview:

| Operation | Expression | Example |
|-----------|-----------|---------|
| Check if number is power of 2 | `n & (n - 1) == 0` | 8 (1000) & 7 (0111) = 0 |
| Get lowest set bit | `n & (-n)` | 12 (1100) → 4 (0100) |
| Remove lowest set bit | `n & (n - 1)` | 12 (1100) → 8 (1000) |
| Set bit at position k | `n | (1 << k)` | Set bit 2 of 5 (101): 101 | 100 = 101 |
| Clear bit at position k | `n & ~(1 << k)` | Clear bit 2 of 7 (111): 111 & 011 = 011 |
| Toggle bit at position k | `n ^ (1 << k)` | Toggle bit 1 of 5 (101): 101 ^ 010 = 111 |
| Check bit at position k | `(n >> k) & 1` | Bit 2 of 5 (101): 101 >> 2 = 1, 1 & 1 = 1 |
| Count set bits | Brian Kernighan: `while(n) { count++; n &= n-1; }` | |
| XOR property | `a ^ a = 0`, `a ^ 0 = a` | Used in Single Number |
| Two's complement | `-n = ~n + 1` | |

## Edge Cases

1. **n = 0**: Return [0].
2. **n = 1**: Return [0, 1].
3. **Large n**: The O(n) approach handles up to 10^5 easily.

## Interview Tips

1. **Start by explaining the pattern**: "Each number's bit count relates to a previously computed number."
2. **Know all three DP recurrences**: The interviewer might ask for alternatives.
3. **The `n & (n-1)` trick is universal**: It appears in "Power of Two," "Counting Bits," and Brian Kernighan's algorithm.
4. **Follow-up**: "How many bits differ between two numbers?" → `countBits(a ^ b)` (Hamming distance).

## Related Problems
- Number of 1 Bits (Hamming Weight)
- Single Number (XOR)
- Hamming Distance
- Power of Two
- Reverse Bits
- Bitwise AND of Numbers Range

