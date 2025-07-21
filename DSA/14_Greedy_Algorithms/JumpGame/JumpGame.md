# Jump Game (Greedy Approach)

## Problem Statement
You are given an integer array nums. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position. Return true if you can reach the last index, and false otherwise.

## Example
```
Input: nums = [2,3,1,1,4]
Output: true
Explanation: Jump 1 step from index 0 to 1, then 3 steps to the last index.

Input: nums = [3,2,1,0,4]
Output: false
Explanation: You will always arrive at index 3. Its max jump length is 0, which makes it impossible to reach the last index.
```

## Greedy Approach (Optimal!)

### Key Insight:
**We don't need to find the exact path - just determine if the end is reachable.**

### How it works:
1. **Track farthest reachable position** as we iterate
2. **For each position**, update farthest based on jump capacity
3. **If current position > farthest**, we're stuck
4. **If farthest >= last index**, we can reach the end

### Key Logic:
```java
public boolean canJump(int[] nums) {
    int farthest = 0;
    
    for (int i = 0; i < nums.length; i++) {
        // If current position is beyond what we can reach
        if (i > farthest) {
            return false;
        }
        
        // Update farthest reachable position
        farthest = Math.max(farthest, i + nums[i]);
        
        // Early termination if we can reach the end
        if (farthest >= nums.length - 1) {
            return true;
        }
    }
    
    return true;
}
```

### Time & Space Complexity:
- **Time:** O(n) - Single pass through array
- **Space:** O(1) - Only using variables

## Why This is Greedy:

### Greedy Choice:
**At each step, we try to reach as far as possible.** This is optimal because:
1. **More reach is always better** - gives more options later
2. **No benefit in jumping shorter** when we can jump farther
3. **Early termination** when end is reachable

### Proof of Correctness:
1. **If farthest < i**, then position i is unreachable from start
2. **If farthest >= n-1**, then end is definitely reachable
3. **Greedy choice is optimal** - maximizing reach at each step gives best chance

## Step-by-Step Visualization:
```
nums = [2,3,1,1,4]
i=0: farthest = max(0, 0+2) = 2
     Can reach indices 0, 1, 2
     
i=1: farthest = max(2, 1+3) = 4
     Can reach indices 0, 1, 2, 3, 4 (>= 4, so can reach end!)

nums = [3,2,1,0,4]
i=0: farthest = max(0, 0+3) = 3
i=1: farthest = max(3, 1+2) = 3  
i=2: farthest = max(3, 2+1) = 3
i=3: farthest = max(3, 3+0) = 3
i=4: i=4 > farthest=3, return false
```

## Alternative Greedy Formulations:

### Backward Greedy:
```java
// Start from end, work backwards
int lastPos = nums.length - 1;
for (int i = nums.length - 2; i >= 0; i--) {
    if (i + nums[i] >= lastPos) {
        lastPos = i;
    }
}
return lastPos == 0;
```

### Range-based Greedy:
```java
// Track current range and next range
int currentEnd = 0, farthest = 0;
for (int i = 0; i < nums.length - 1; i++) {
    farthest = Math.max(farthest, i + nums[i]);
    if (i == currentEnd) {
        currentEnd = farthest;
    }
}
return currentEnd >= nums.length - 1;
```

## Greedy vs Dynamic Programming:

### DP Characteristics:
- **Overlapping subproblems** - same positions visited multiple times
- **Optimal substructure** - optimal solution uses optimal subsolutions
- **Space complexity:** O(n) for memoization

### Greedy Characteristics:
- **Greedy choice property** - local optimum leads to global optimum
- **No overlapping subproblems** - each position processed once
- **Space complexity:** O(1)

### Why Greedy Works Here:
**The problem has greedy choice property** - choosing maximum reach at each step is always optimal for reachability.

## Edge Cases:
1. **Single element** [5] → Always true
2. **First element is 0** [0] → True only if array length is 1
3. **Large jumps** [10,0,0,0,0] → Can skip zeros
4. **All zeros except first** [1,0,0,0] → Check reachability

## Related Greedy Problems:

### Jump Game II (Minimum Jumps):
- **Find minimum jumps** to reach end
- **BFS-like greedy** approach

### Jump Game with Cost:
- **Each jump has cost** - minimize total cost
- **May need DP** if greedy choice property doesn't hold

## LeetCode Similar Problems:
- [55. Jump Game](https://leetcode.com/problems/jump-game/) (this problem)
- [45. Jump Game II](https://leetcode.com/problems/jump-game-ii/)
- [1306. Jump Game III](https://leetcode.com/problems/jump-game-iii/)
- [134. Gas Station](https://leetcode.com/problems/gas-station/)
- [122. Best Time to Buy and Sell Stock II](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/)

## Interview Tips:
- Emphasize the greedy choice: "always reach as far as possible"
- Explain why this greedy choice is optimal
- Compare with DP approach to show optimization
- Handle edge cases systematically
- This is a classic example of greedy algorithm design 