# Single Number Series

## Problem Statement
Given a non-empty array of integers `nums`, every element appears twice except for one. Find that single one.

**Constraint**: You must implement a solution with **linear runtime complexity** and use only **constant extra space**.

**Examples:**
```
Input: nums = [2,2,1]
Output: 1

Input: nums = [4,1,2,1,2]  
Output: 4

Input: nums = [1]
Output: 1
```

## Problem Analysis

### Core Insight
This problem is a **perfect application of XOR operation** because:
- **a ⊕ a = 0** (any number XORed with itself is 0)
- **a ⊕ 0 = a** (any number XORed with 0 is itself)
- **XOR is commutative and associative** (order doesn't matter)

### Why XOR Works
If we XOR all numbers: `a ⊕ b ⊕ a ⊕ c ⊕ b`
- Rearranging: `(a ⊕ a) ⊕ (b ⊕ b) ⊕ c`
- Simplifying: `0 ⊕ 0 ⊕ c = c`

## Problem Variations

### Single Number I: Appears Twice Pattern

#### Approach: XOR All Elements ⭐

```java
public int singleNumber(int[] nums) {
    int result = 0;
    for (int num : nums) {
        result ^= num;
    }
    return result;
}
```

#### Why This Works
- **Duplicates cancel out**: Each pair `x ⊕ x = 0`
- **Single element remains**: `0 ⊕ single = single`
- **Order independent**: XOR is commutative

#### Time Complexity: **O(n)** - single pass
#### Space Complexity: **O(1)** - only one variable

### Single Number II: Appears Three Times Pattern

#### Problem Extension
One element appears **once**, all others appear **three times**.

#### Approach 1: State Machine ⭐ (Advanced)

```java
public int singleNumberII(int[] nums) {
    int ones = 0, twos = 0;
    
    for (int num : nums) {
        // Update twos: elements seen twice
        twos |= (ones & num);
        
        // Update ones: elements seen once
        ones ^= num;
        
        // Clear elements seen three times
        int threes = ones & twos;
        ones &= ~threes;
        twos &= ~threes;
    }
    
    return ones;
}
```

#### State Machine Logic
- **ones**: tracks bits appearing 1 time
- **twos**: tracks bits appearing 2 times  
- **threes**: identifies bits appearing 3 times (to be cleared)

#### State Transitions
```
State 00 (0 times) + bit → State 01 (1 time)
State 01 (1 time)  + bit → State 10 (2 times)  
State 10 (2 times) + bit → State 00 (0 times, reset)
```

#### Approach 2: Bit Counting (More Intuitive)

```java
public int singleNumberIIBitCount(int[] nums) {
    int result = 0;
    
    // Check each bit position
    for (int i = 0; i < 32; i++) {
        int count = 0;
        
        // Count how many numbers have bit i set
        for (int num : nums) {
            if ((num >> i & 1) == 1) {
                count++;
            }
        }
        
        // If not divisible by 3, single number has this bit
        if (count % 3 != 0) {
            result |= (1 << i);
        }
    }
    
    return result;
}
```

#### Key Insight
- **For each bit position**: Count how many numbers have that bit set
- **If count % 3 ≠ 0**: The single number contributes to this count
- **Reconstruct number**: Set corresponding bit in result

### Single Number III: Two Unique Elements

#### Problem Extension  
**Two elements** appear once, all others appear twice.

#### Approach: XOR + Bit Separation ⭐

```java
public int[] singleNumberIII(int[] nums) {
    // Step 1: XOR all numbers → gets a ⊕ b
    int xor = 0;
    for (int num : nums) {
        xor ^= num;
    }
    
    // Step 2: Find any set bit in xor
    int rightmostSetBit = xor & (-xor);
    
    // Step 3: Separate numbers into two groups
    int num1 = 0, num2 = 0;
    for (int num : nums) {
        if ((num & rightmostSetBit) != 0) {
            num1 ^= num; // Group 1
        } else {
            num2 ^= num; // Group 2
        }
    }
    
    return new int[]{num1, num2};
}
```

#### Algorithm Explanation
1. **XOR all numbers**: Result is `a ⊕ b` (two unique numbers)
2. **Find differentiating bit**: Any set bit in `a ⊕ b` means `a` and `b` differ at that position
3. **Separate into groups**: Use this bit to divide all numbers into two groups
4. **XOR each group**: Each group will have one unique number + pairs

#### Rightmost Set Bit Trick
```java
rightmostSetBit = xor & (-xor);
```
- **-xor**: Two's complement flips bits and adds 1
- **xor & (-xor)**: Isolates rightmost set bit
- **Example**: xor = 6 (110₂), -xor = -6, xor & (-xor) = 2 (010₂)

## Bit Manipulation Fundamentals

### Essential XOR Properties
```java
a ⊕ a = 0     // Self-cancellation
a ⊕ 0 = a     // Identity  
a ⊕ b = b ⊕ a // Commutative
(a ⊕ b) ⊕ c = a ⊕ (b ⊕ c) // Associative
```

### Key Bit Operations
```java
x & 1          // Check if x is odd (last bit)
x >> 1         // Divide by 2 (right shift)
x << 1         // Multiply by 2 (left shift)  
x & (x-1)      // Clear rightmost set bit
x & (-x)       // Isolate rightmost set bit
~x             // Flip all bits (bitwise NOT)
x |= (1 << i)  // Set bit i
x &= ~(1 << i) // Clear bit i
(x >> i) & 1   // Check if bit i is set
```

## Example Traces

### Single Number I: [4,1,2,1,2]
```
result = 0
result = 0 ⊕ 4 = 4   (100₂)
result = 4 ⊕ 1 = 5   (101₂)  
result = 5 ⊕ 2 = 7   (111₂)
result = 7 ⊕ 1 = 6   (110₂)
result = 6 ⊕ 2 = 4   (100₂)
Answer: 4
```

### Single Number II: [2,2,3,2]  
**Bit counting approach**:
```
Bit 0: 0+0+1+0 = 1 → 1%3 = 1 → set bit 0 in result
Bit 1: 1+1+1+1 = 4 → 4%3 = 1 → set bit 1 in result  
Result: 11₂ = 3
```

### Single Number III: [1,2,1,3,2,5]
```
Step 1: XOR all → 1⊕2⊕1⊕3⊕2⊕5 = 3⊕5 = 6 (110₂)
Step 2: Rightmost set bit → 6 & (-6) = 2 (010₂)
Step 3: Group by bit 1:
  Group 1 (bit 1 set): 2,3,2 → 2⊕3⊕2 = 3
  Group 2 (bit 1 not set): 1,1,5 → 1⊕1⊕5 = 5
Answer: [3,5]
```

## Alternative Approaches (For Comparison)

### HashMap Approach
```java
public int singleNumberHashMap(int[] nums) {
    Map<Integer, Integer> frequency = new HashMap<>();
    for (int num : nums) {
        frequency.put(num, frequency.getOrDefault(num, 0) + 1);
    }
    
    for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
        if (entry.getValue() == 1) {
            return entry.getKey();
        }
    }
    return -1;
}
```
- **Time**: O(n), **Space**: O(n)
- **Not optimal** due to space usage

### Set Approach  
```java
public int singleNumberSet(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int num : nums) {
        if (seen.contains(num)) {
            seen.remove(num);
        } else {
            seen.add(num);
        }
    }
    return seen.iterator().next();
}
```
- **Time**: O(n), **Space**: O(n)
- **Clever** but still uses extra space

## Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| XOR | O(n) | O(1) | Optimal, elegant | Requires understanding XOR |
| HashMap | O(n) | O(n) | Intuitive | Extra space |
| Set | O(n) | O(n) | Clean logic | Extra space |
| Bit Counting | O(32n) | O(1) | Works for any frequency | More complex |

## General Pattern Recognition

### When to Use Each Approach
- **Frequency = 2**: Use XOR directly
- **Frequency = 3**: Use bit counting or state machine
- **Multiple unique elements**: Use bit separation
- **General frequency m**: Use bit counting with modulo m

### Template for General Case
```java
public int singleNumberGeneral(int[] nums, int frequency) {
    int result = 0;
    
    for (int i = 0; i < 32; i++) {
        int count = 0;
        for (int num : nums) {
            if ((num >> i & 1) == 1) {
                count++;
            }
        }
        
        if (count % frequency != 0) {
            result |= (1 << i);
        }
    }
    
    return result;
}
```

## Interview Tips

1. **Start with XOR properties** - explain why it works for pairs
2. **Demonstrate with small example** - show step-by-step XOR
3. **Discuss space constraint** - why HashMap/Set don't meet requirements
4. **Handle variations** - be ready for "appears 3 times" follow-up
5. **Explain bit manipulation** - show understanding of binary operations

## Common Mistakes

1. **Forgetting commutativity** - thinking order matters in XOR
2. **Wrong bit operations** - using | instead of ⊕ 
3. **Overflow concerns** - not considering negative numbers in bit counting
4. **Edge cases** - single element array, all same except one

## Mathematical Insight

The Single Number problems beautifully demonstrate:
- **Group theory**: XOR forms an Abelian group
- **Linear algebra**: XOR is addition in GF(2) field
- **Modular arithmetic**: Bit counting uses mod operations
- **Information theory**: XOR is self-inverse operation

## LeetCode Similar Problems:
- [137. Single Number II](https://leetcode.com/problems/single-number-ii/)
- [260. Single Number III](https://leetcode.com/problems/single-number-iii/)
- [268. Missing Number](https://leetcode.com/problems/missing-number/)
- [287. Find the Duplicate Number](https://leetcode.com/problems/find-the-duplicate-number/)
- [389. Find the Difference](https://leetcode.com/problems/find-the-difference/)

The elegance of the XOR solution makes this a **classic demonstration** of how understanding mathematical properties can lead to surprisingly efficient algorithms! 