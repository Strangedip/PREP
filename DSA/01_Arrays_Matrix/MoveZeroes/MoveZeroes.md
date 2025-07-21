# Move Zeroes

## Problem Statement

Given an integer array, move all zeros to the end while maintaining the relative order of non-zero elements. The operation must be done in-place without making a copy of the array.

**Constraints:**
- Must be done in-place (no extra array)
- Maintain relative order of non-zero elements
- All zeros should be at the end

## Example
```
Input: nums = [0,1,0,3,12]
Output: [1,3,12,0,0]
Explanation: All non-zeros moved to front, zeros moved to end, order preserved
```

## Approach 1: Brute Force (Not In-Place)

### How it works:
Create a new structure to separate non-zero and zero elements.

1. Collect all non-zero elements in order
2. Count the number of zeros
3. Copy non-zeros back to original array
4. Fill remaining positions with zeros

### Code Logic:
```java
List<Integer> nonZero = new ArrayList<>();
int zeroCount = 0;

for (int num : nums) {
    if (num != 0) {
        nonZero.add(num);
    } else {
        zeroCount++;
    }
}

// Copy back non-zeros, then fill zeros
```

### Complexity:
- **Time:** O(n)
- **Space:** O(n) - Uses extra list

### When to use:
- When in-place constraint is not required
- To understand the problem logic

## Approach 2: Two Pointers (Optimal!)

### The Big Idea (Simple Explanation):
Think of this like organizing a bookshelf. You have two people: one person (the "reader") goes through every book, and another person (the "organizer") only places the good books in the correct positions from the left. After placing all good books, the remaining spaces are filled with "empty slots" (zeros).

**Key Insight:** Use one pointer to iterate through the array and another pointer to track where the next non-zero element should be placed.

### How it works:
1. **insertPos pointer:** Tracks where to place the next non-zero element
2. **Current pointer (i):** Iterates through the entire array
3. When we find a non-zero element, place it at insertPos and increment insertPos
4. After processing all elements, fill remaining positions with zeros

### Step-by-step Walkthrough:
```
nums = [0,1,0,3,12]
insertPos = 0

i=0: nums[0]=0 (zero), skip, insertPos=0
i=1: nums[1]=1 (non-zero), nums[0]=1, insertPos=1
i=2: nums[2]=0 (zero), skip, insertPos=1  
i=3: nums[3]=3 (non-zero), nums[1]=3, insertPos=2
i=4: nums[4]=12 (non-zero), nums[2]=12, insertPos=3

Array after moving non-zeros: [1,3,12,3,12]
Fill positions 3,4 with zeros: [1,3,12,0,0]
```

### Code Logic:
```java
int insertPos = 0;

// Move all non-zero elements to the front
for (int i = 0; i < nums.length; i++) {
    if (nums[i] != 0) {
        nums[insertPos] = nums[i];
        insertPos++;
    }
}

// Fill remaining positions with zeros
while (insertPos < nums.length) {
    nums[insertPos] = 0;
    insertPos++;
}
```

### Complexity:
- **Time:** O(n) - Single pass through array
- **Space:** O(1) - Only using two pointers

### When to use:
- **This is the standard interview solution**
- When you need in-place operation
- When you want optimal space complexity

## Approach 3: Swap Method

### How it works:
Instead of overwriting, swap non-zero elements with elements at the correct position.

### Code Logic:
```java
int insertPos = 0;

for (int i = 0; i < nums.length; i++) {
    if (nums[i] != 0) {
        // Swap current element with element at insertPos
        int temp = nums[i];
        nums[i] = nums[insertPos];
        nums[insertPos] = temp;
        insertPos++;
    }
}
```

### When to use:
- When you want to minimize write operations
- When elements are expensive to copy

## Approach 4: Optimized Swap (Avoid Unnecessary Swaps)

### How it works:
Only perform swaps when positions are different to avoid swapping element with itself.

### Code Logic:
```java
int insertPos = 0;

for (int i = 0; i < nums.length; i++) {
    if (nums[i] != 0) {
        if (i != insertPos) {
            // Only swap if positions are different
            int temp = nums[i];
            nums[i] = nums[insertPos];
            nums[insertPos] = temp;
        }
        insertPos++;
    }
}
```

### Benefits:
- Reduces unnecessary operations
- Slightly better performance in practice

## Interview Strategy

### Step-by-step Approach:
1. **Clarify requirements:** "I need to move zeros to the end in-place, right?"
2. **Start with concept:** "I could collect non-zeros and then fill zeros..."
3. **Optimize for in-place:** "But I can do this in-place using two pointers..."
4. **Explain the approach:** "One pointer tracks where to place non-zeros..."
5. **Code the solution:** Implement the two pointers method
6. **Test edge cases:** Empty array, all zeros, no zeros

### What Interviewers Look For:
- Understanding of in-place operation requirement
- Recognition of two pointers optimization
- Correct handling of the filling zeros step
- Clean, bug-free implementation

## Edge Cases

### Case 1: All Zeros
```
Input: [0,0,0]
Output: [0,0,0]
```

### Case 2: No Zeros
```
Input: [1,2,3]
Output: [1,2,3]
```

### Case 3: Single Element
```
Input: [0] → Output: [0]
Input: [5] → Output: [5]
```

### Case 4: Zeros at Beginning
```
Input: [0,0,1,2,3]
Output: [1,2,3,0,0]
```

### Case 5: Zeros at End
```
Input: [1,2,3,0,0]
Output: [1,2,3,0,0] (already correct)
```

## Common Mistakes

1. **Forgetting to fill zeros:** Only moving non-zeros without filling remaining positions
2. **Wrong pointer management:** Incrementing insertPos when encountering zeros
3. **Not preserving order:** Using unstable sorting or wrong swapping logic
4. **Modifying during iteration:** Changing array structure while iterating

## Optimizations & Variations

### Optimization 1: Early Termination
```java
// If we've placed all non-zeros, remaining are already zeros
if (insertPos == countNonZeros) break;
```

### Optimization 2: Count-Based Approach
```java
// First count non-zeros, then you know exactly how many zeros to fill
int nonZeroCount = 0;
for (int num : nums) {
    if (num != 0) nonZeroCount++;
}
```

### Variation: Move Specific Element
This pattern can be generalized to move any specific element (not just zeros) to the end.

## Real-world Applications

1. **Data Cleanup:** Removing null/empty entries from datasets
2. **Memory Management:** Compacting memory by moving unused blocks
3. **File Systems:** Defragmentation algorithms
4. **Graphics Programming:** Removing transparent pixels
5. **Database Operations:** Cleaning sparse data structures

## Pattern Recognition

This problem teaches:
- **Two pointers technique** for in-place operations
- **Partitioning arrays** based on conditions
- **Stable partitioning** (maintaining relative order)
- **In-place algorithms** design

## Follow-up Questions

**Q: What if we want to move zeros to the beginning?**
A: Iterate from right to left or use similar logic with different pointer movement.

**Q: What if we want to move multiple different values?**
A: Extend to multi-way partitioning using multiple pointers.

**Q: What if we want to count minimum swaps needed?**
A: Track the number of swaps performed in the optimized swap approach.

## Note

**For Mid-Level Interviews (2+ years):**
- **Master the two pointers approach:** This is the expected optimal solution
- **Understand in-place operations:** Know why we can't use extra arrays
- **Handle edge cases cleanly:** Empty arrays, all zeros, no zeros
- **Know multiple approaches:** Be able to discuss swap vs overwrite methods
- **Explain time/space complexity:** O(n) time, O(1) space

**Interview Red Flags:**
- Using extra arrays when in-place is required
- Not preserving the relative order of non-zero elements
- Forgetting to fill the remaining positions with zeros
- Inefficient approaches like bubble sort style

**Remember:** This problem is a fundamental example of array partitioning. The two pointers technique used here appears in many other problems like removing duplicates, segregating even/odd numbers, and Dutch National Flag problem!

**Pattern Connection:** Once you master this, you can easily solve similar problems like "Remove Element", "Remove Duplicates", and "Sort Colors"! 