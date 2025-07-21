# Next Greater Element I

## Problem Statement
Given two distinct arrays nums1 and nums2, where nums1 is a subset of nums2, find all the next greater elements for nums1's elements in nums2.

## Example
```
Input: nums1 = [4,1,2], nums2 = [1,3,4,2]
Output: [-1,3,-1]
Explanation:
- For 4: No greater element → -1
- For 1: Next greater is 3
- For 2: No greater element → -1
```

## Approach: Monotonic Stack + HashMap

### How it works:
1. **Process nums2** to find next greater for all elements
2. **Use monotonic stack** to efficiently find next greater
3. **Store results in HashMap** for O(1) lookup
4. **Build answer** by looking up nums1 elements

### Key Logic:
```java
// Step 1: Build next greater mapping for nums2
Map<Integer, Integer> nextGreater = new HashMap<>();
Stack<Integer> stack = new Stack<>();

for (int num : nums2) {
    // While current num is greater than stack elements
    while (!stack.isEmpty() && num > stack.peek()) {
        nextGreater.put(stack.pop(), num);
    }
    stack.push(num);
}

// Remaining elements have no greater element
while (!stack.isEmpty()) {
    nextGreater.put(stack.pop(), -1);
}

// Step 2: Build result for nums1
int[] result = new int[nums1.length];
for (int i = 0; i < nums1.length; i++) {
    result[i] = nextGreater.get(nums1[i]);
}

return result;
```

### Time & Space Complexity:
- **Time:** O(m + n) where m = nums1.length, n = nums2.length
- **Space:** O(n) for HashMap and stack

## Stack Visualization:
```
nums2 = [1,3,4,2]

Process 1: stack = [1], nextGreater = {}
Process 3: 3 > 1, so nextGreater[1] = 3
           stack = [3], nextGreater = {1: 3}
Process 4: 4 > 3, so nextGreater[3] = 4
           stack = [4], nextGreater = {1: 3, 3: 4}
Process 2: 2 < 4, so stack = [4, 2]
           Final nextGreater = {1: 3, 3: 4, 4: -1, 2: -1}
```

## Pattern Recognition:

### This is a classic "Next Greater Element" problem:
- **Monotonic decreasing stack** (stack top is smallest)
- **When larger element found**, it resolves multiple previous elements
- **Common in problems** requiring future lookups

### Why Monotonic Stack Works:
1. **Maintains decreasing order** in stack
2. **Larger element** resolves all smaller elements before it
3. **No need to revisit** resolved elements

## Alternative: Brute Force

### For comparison:
```java
// O(m * n) solution
for (int i = 0; i < nums1.length; i++) {
    int target = nums1[i];
    int nextGreater = -1;
    boolean found = false;
    
    for (int j = 0; j < nums2.length; j++) {
        if (found && nums2[j] > target) {
            nextGreater = nums2[j];
            break;
        }
        if (nums2[j] == target) {
            found = true;
        }
    }
    result[i] = nextGreater;
}
```

## Follow-up Variations:

### Next Greater Element II (Circular):
- **Array is circular** → Use modular arithmetic
- **Process array twice** with same stack logic

### Previous Greater Element:
- **Same logic** but process from right to left
- **Or reverse array** and find next greater

## LeetCode Similar Problems:
- [503. Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/)
- [739. Daily Temperatures](https://leetcode.com/problems/daily-temperatures/)
- [556. Next Greater Element III](https://leetcode.com/problems/next-greater-element-iii/)
- [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)

## Interview Tips:
- Start with brute force to show understanding
- Optimize using monotonic stack pattern
- Explain why stack maintains decreasing order
- Handle edge cases: no greater element exists
- This pattern appears frequently in stack problems 