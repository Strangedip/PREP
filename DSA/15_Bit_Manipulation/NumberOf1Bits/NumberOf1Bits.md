# Number of 1 Bits (Hamming Weight)

## Problem Statement
Write a function that takes an **unsigned integer** and returns the number of '1' bits it has (also known as the **Hamming weight**).

**Examples:**
```
Input: n = 00000000000000000000000000001011 (11 in decimal)
Output: 3
Explanation: The input binary string has three '1' bits.

Input: n = 00000000000000000000000010000000 (128 in decimal)  
Output: 1
Explanation: The input binary string has one '1' bit.
```

## Problem Analysis

### Core Insight
Count the number of **set bits** (1s) in the binary representation of an integer. This is a fundamental operation in computer science with applications in:
- **Cryptography** (Hamming distance)
- **Error correction** (parity bits)
- **Database indexing** (bitmap operations)
- **Compiler optimizations** (register allocation)

### Key Challenges
- **Signed vs unsigned integers** in Java
- **Negative numbers** in two's complement representation
- **Performance optimization** for frequent operations

## Approaches

### Approach 1: Check Each Bit ⭐ (Most Intuitive)

#### Key Insight
**Examine each bit position** individually using bit masking.

#### Algorithm
```java
public int hammingWeight(int n) {
    int count = 0;
    
    // Check each of the 32 bits
    for (int i = 0; i < 32; i++) {
        if ((n & (1 << i)) != 0) {
            count++;
        }
    }
    
    return count;
}
```

#### How It Works
- **Bit mask**: `1 << i` creates mask with only bit `i` set
- **AND operation**: `n & mask` isolates bit `i`
- **Check result**: Non-zero means bit is set

#### Time Complexity
- **O(32) = O(1)** - Always check 32 bits regardless of input

#### Space Complexity
- **O(1)** - Only use constant extra space

### Approach 2: Right Shift Method

#### Key Insight
**Process bits from right to left** by repeatedly checking the rightmost bit.

```java
public int hammingWeightShift(int n) {
    int count = 0;
    
    while (n != 0) {
        count += (n & 1); // Check rightmost bit
        n >>>= 1;         // Unsigned right shift
    }
    
    return count;
}
```

#### Critical Detail: Unsigned Right Shift
- **`>>>`**: Unsigned right shift (fills with 0s)
- **`>>`**: Signed right shift (fills with sign bit)
- **Why important**: Handles negative numbers correctly

#### Time Complexity
- **O(log n)** - Stops when all remaining bits are 0

### Approach 3: Brian Kernighan's Algorithm ⭐⭐ (Most Efficient)

#### Key Insight
**Clear the rightmost set bit** in each iteration using `n & (n-1)`.

```java
public int hammingWeightOptimal(int n) {
    int count = 0;
    
    while (n != 0) {
        n &= (n - 1); // Clear rightmost set bit
        count++;
    }
    
    return count;
}
```

#### Why `n & (n-1)` Works
**Mathematical property**: `n & (n-1)` always clears the rightmost set bit.

**Example**: n = 12 (1100₂)
```
n     = 1100
n-1   = 1011  
n&(n-1) = 1000  (rightmost 1 in n is cleared)
```

**Proof**: 
- If rightmost bit of `n` is 0: `n-1` flips trailing zeros to 1s and rightmost 1 to 0
- If rightmost bit of `n` is 1: `n-1` just turns it to 0
- AND operation keeps only the unchanged prefix

#### Time Complexity
- **O(k)** where k = number of set bits
- **Best case**: O(1) when n = 0 or power of 2
- **Worst case**: O(32) when all bits are set

#### Why It's Optimal
**Only iterates for actual set bits**, not all bit positions.

### Approach 4: Parallel Bit Counting (SWAR)

#### Key Insight
**Count bits in parallel** using SIMD-Within-A-Register techniques.

```java
public int hammingWeightParallel(int n) {
    // Count bits in pairs
    n = n - ((n >>> 1) & 0x55555555);
    
    // Count bits in groups of 4  
    n = (n & 0x33333333) + ((n >>> 2) & 0x33333333);
    
    // Count bits in groups of 8
    n = (n + (n >>> 4)) & 0x0F0F0F0F;
    
    // Count bits in groups of 16
    n = n + (n >>> 8);
    
    // Count bits in groups of 32
    n = n + (n >>> 16);
    
    return n & 0x3F; // Mask to get final count
}
```

#### Algorithm Breakdown

**Step 1: Count in pairs**
- **Mask**: `0x55555555` = `01010101...` (alternating bits)
- **Logic**: Each 2-bit group becomes count of 1s in that group

**Step 2: Count in groups of 4**
- **Mask**: `0x33333333` = `00110011...` 
- **Logic**: Add adjacent 2-bit counts

**Step 3: Count in groups of 8**
- **Mask**: `0x0F0F0F0F` = `00001111...`
- **Logic**: Add adjacent 4-bit counts

**Steps 4-5: Combine results**
- **No masking needed** (results fit in allocated bits)
- **Final mask**: `0x3F` = 63 (max possible count for 32 bits)

#### Time Complexity
- **O(1)** - Fixed number of operations
- **Highly optimized** for modern processors

### Approach 5: Lookup Table

#### Key Insight
**Pre-compute results** for small bit patterns and combine.

```java
private static final int[] BIT_COUNT_TABLE = new int[256];

static {
    for (int i = 0; i < 256; i++) {
        BIT_COUNT_TABLE[i] = (i & 1) + BIT_COUNT_TABLE[i >>> 1];
    }
}

public int hammingWeightLookup(int n) {
    return BIT_COUNT_TABLE[n & 0xFF] +           // Last 8 bits
           BIT_COUNT_TABLE[(n >>> 8) & 0xFF] +  // Next 8 bits
           BIT_COUNT_TABLE[(n >>> 16) & 0xFF] + // Next 8 bits
           BIT_COUNT_TABLE[(n >>> 24) & 0xFF];  // First 8 bits
}
```

#### Trade-offs
- **Space**: 256 integers (1KB) for lookup table
- **Time**: O(1) with 4 table lookups + 3 additions
- **Use case**: When function called frequently

## Example Traces

### Brian Kernighan for n = 11 (1011₂)

```
Step 1: 1011 & 1010 = 1010  (cleared rightmost 1)
Step 2: 1010 & 1001 = 1000  (cleared rightmost 1)  
Step 3: 1000 & 0111 = 0000  (cleared rightmost 1)

Total iterations: 3 (= number of 1 bits)
```

### Parallel Counting for n = 11 (1011₂)

```
Original:     00000000000000000000000000001011
Step 1:       00000000000000000000000000001010  (count in pairs)
Step 2:       00000000000000000000000000000011  (count in 4s)
Step 3:       00000000000000000000000000000011  (count in 8s)
Step 4:       00000000000000000000000000000011  (count in 16s)
Step 5:       00000000000000000000000000000011  (final result)

Final count: 3
```

## Comparison of Approaches

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Check Each Bit | O(32) | O(1) | Simple, intuitive | Always 32 operations |
| Right Shift | O(log n) | O(1) | Stops early for sparse bits | Variable performance |
| Brian Kernighan | O(k) | O(1) | Optimal for sparse bits | Slightly complex |
| Parallel Counting | O(1) | O(1) | Consistent performance | Complex bit manipulation |
| Lookup Table | O(1) | O(256) | Very fast | Memory overhead |
| Built-in | O(1) | O(1) | Hardware optimized | Language dependent |

*k = number of set bits*

## Advanced Bit Manipulation Tricks

### Power of 2 Detection
```java
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}
```
**Why it works**: Powers of 2 have exactly one bit set.

### Isolate Rightmost Set Bit
```java
int rightmostSetBit = n & (-n);
```
**Two's complement property**: `-n` flips all bits and adds 1.

### Clear Rightmost Set Bit  
```java
int cleared = n & (n - 1);
```
**Core of Brian Kernighan's algorithm**.

### Count Trailing Zeros
```java
int countTrailingZeros(int n) {
    if (n == 0) return 32;
    int count = 0;
    while ((n & 1) == 0) {
        n >>>= 1;
        count++;
    }
    return count;
}
```

## Hardware and Compiler Optimizations

### Built-in Functions
Most modern languages provide optimized bit counting:
- **Java**: `Integer.bitCount(n)`
- **C/C++**: `__builtin_popcount(n)`
- **Python**: `bin(n).count('1')`

### Hardware Instructions
- **x86**: `POPCNT` instruction (population count)
- **ARM**: `CNT` instruction
- **GPU**: Parallel bit operations

### When to Use Each Approach
- **General use**: Brian Kernighan's algorithm
- **Hot paths**: Built-in functions or parallel counting
- **Educational**: Check each bit method
- **Memory-rich environments**: Lookup tables

## Interview Tips

1. **Start with simple approach** - check each bit method
2. **Optimize step by step** - explain Brian Kernighan's insight
3. **Discuss trade-offs** - time vs space vs complexity
4. **Handle edge cases** - zero, negative numbers, all bits set
5. **Mention hardware optimization** - shows systems knowledge

## Common Mistakes

1. **Using signed right shift `>>`** instead of unsigned `>>>`
2. **Infinite loops** with negative numbers and wrong shift
3. **Off-by-one errors** in bit position calculations
4. **Overflow** in intermediate calculations for large numbers

## Applications

### Real-world Uses
- **Database bitmap indexes** - count set bits for cardinality
- **Compression algorithms** - Hamming weights in error correction
- **Machine learning** - sparse vector operations
- **Cryptography** - key strength analysis
- **Computer graphics** - alpha channel operations

## LeetCode Similar Problems:
- [338. Counting Bits](https://leetcode.com/problems/counting-bits/)
- [136. Single Number](https://leetcode.com/problems/single-number/)
- [190. Reverse Bits](https://leetcode.com/problems/reverse-bits/)
- [342. Power of Four](https://leetcode.com/problems/power-of-four/)
- [231. Power of Two](https://leetcode.com/problems/power-of-two/)

The Number of 1 Bits problem is **fundamental to bit manipulation** and showcases how understanding hardware properties can lead to remarkably efficient algorithms!

## Mathematical Insight

**Hamming weight** is named after Richard Hamming and represents the number of symbols that differ from zero. In binary, it's simply the count of 1s. This connects to:
- **Information theory** - measuring information content
- **Coding theory** - error detection and correction
- **Linear algebra** - sparse matrix operations
- **Number theory** - digital root relationships 