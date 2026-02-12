# Daily Temperatures

## Problem Statement
Given an array of daily temperatures, return an array where each element represents how many days you have to wait until a warmer temperature. If there is no warmer day, return 0.

## Example
```
Input: temperatures = [73,74,75,71,69,72,76,73]
Output: [1,1,4,2,1,1,0,0]
Explanation: 
- Day 0 (73°): Next warmer day is day 1 (74°) → wait 1 day
- Day 1 (74°): Next warmer day is day 2 (75°) → wait 1 day
- Day 2 (75°): Next warmer day is day 6 (76°) → wait 4 days
```

## Approach 1: Brute Force

### How it works:
1. **For each day**, check all subsequent days
2. **Find first day** with higher temperature
3. **Calculate waiting days**

### Time & Space Complexity:
- **Time:** O(n²) - Nested loops
- **Space:** O(1) - Only result array

## Approach 2: Monotonic Stack (Optimal!)

### How it works:
1. **Stack stores indices** of days with decreasing temperatures
2. **When warmer day found**, pop from stack and calculate waiting days
3. **Maintain decreasing temperature order** in stack

### Key Logic:
```java
int[] result = new int[temperatures.length];
Stack<Integer> stack = new Stack<>(); // Store indices

for (int i = 0; i < temperatures.length; i++) {
    // While current temp is warmer than stack top
    while (!stack.isEmpty() && 
           temperatures[i] > temperatures[stack.peek()]) {
        int prevIndex = stack.pop();
        result[prevIndex] = i - prevIndex; // Days to wait
    }
    
    stack.push(i); // Add current day
}

return result;
```

### Time & Space Complexity:
- **Time:** O(n) - Each element pushed/popped once
- **Space:** O(n) - Stack storage

## Stack Visualization:
```
temperatures = [73,74,75,71,69,72,76,73]

i=0, temp=73: stack=[0]
i=1, temp=74: 74>73, pop 0, result[0]=1-0=1, stack=[1]
i=2, temp=75: 75>74, pop 1, result[1]=2-1=1, stack=[2]
i=3, temp=71: 71<75, stack=[2,3]
i=4, temp=69: 69<71, stack=[2,3,4]
i=5, temp=72: 72>69, pop 4, result[4]=5-4=1
              72>71, pop 3, result[3]=5-3=2
              72<75, stack=[2,5]
i=6, temp=76: 76>72, pop 5, result[5]=6-5=1
              76>75, pop 2, result[2]=6-2=4
              stack=[6]
i=7, temp=73: 73<76, stack=[6,7]

Final result: [1,1,4,2,1,1,0,0]
```

## Why Monotonic Stack Works:

### Key Insight:
- **Stack maintains decreasing temperatures**
- **When warmer temperature found**, it resolves all previous colder days
- **No need to look back** once resolved

### Pattern Recognition:
- **"Next Greater Element"** type problem
- **Common in problems requiring future lookups**

## Alternative: Array-based Stack

### For better performance:
```java
int[] stack = new int[temperatures.length];
int stackIndex = -1;

// Use array instead of Stack object
// Slightly better performance
```

## LeetCode Similar Problems:
- [739. Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) (this problem)
- [496. Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/)
- [503. Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/)
- [556. Next Greater Element III](https://leetcode.com/problems/next-greater-element-iii/)
- [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)

## Interview Tips:
- Recognize this as "Next Greater Element" pattern
- Start with brute force to show understanding
- Optimize using monotonic stack concept
- Explain why stack maintains decreasing order
- Practice drawing stack state changes 