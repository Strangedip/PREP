# Jump Game

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

## Approach 1: Greedy (Optimal!)

### How it works:
1. **Track farthest reachable position** as we iterate
2. **Update farthest** based on current position + jump length
3. **If current position > farthest**, we can't proceed
4. **If farthest >= last index**, we can reach the end

### Key Logic:
```java
public boolean canJump(int[] nums) {
    int farthest = 0;
    
    for (int i = 0; i < nums.length; i++) {
        // If current position is beyond reachable area
        if (i > farthest) {
            return false;
        }
        
        // Update farthest reachable position
        farthest = Math.max(farthest, i + nums[i]);
        
        // Early exit if we can reach the end
        if (farthest >= nums.length - 1) {
            return true;
        }
    }
    
    return true;
}
```

### Time & Space Complexity:
- **Time:** O(n) - Single pass through array
- **Space:** O(1) - Only using a few variables

## Approach 2: Dynamic Programming

### How it works:
1. **dp[i] = true** if position i is reachable
2. **dp[0] = true** (starting position)
3. **For each reachable position**, mark all positions within jump range as reachable

### Key Logic:
```java
public boolean canJump(int[] nums) {
    boolean[] dp = new boolean[nums.length];
    dp[0] = true;
    
    for (int i = 0; i < nums.length; i++) {
        if (!dp[i]) continue; // Skip unreachable positions
        
        // Mark all positions within jump range as reachable
        for (int j = 1; j <= nums[i] && i + j < nums.length; j++) {
            dp[i + j] = true;
        }
    }
    
    return dp[nums.length - 1];
}
```

### Time & Space Complexity:
- **Time:** O(n * m) where m is average jump length
- **Space:** O(n) - DP array

## Approach 3: Backward DP

### How it works:
1. **Start from last position** (always reachable)
2. **For each position**, check if any position within jump range is reachable
3. **Work backwards** to first position

### Key Logic:
```java
public boolean canJump(int[] nums) {
    boolean[] dp = new boolean[nums.length];
    dp[nums.length - 1] = true; // Last position is reachable
    
    for (int i = nums.length - 2; i >= 0; i--) {
        // Check if any position within jump range is reachable
        for (int j = 1; j <= nums[i] && i + j < nums.length; j++) {
            if (dp[i + j]) {
                dp[i] = true;
                break;
            }
        }
    }
    
    return dp[0];
}
```

## Why Greedy Works:

### Key Insight:
- **We only need to know** if the end is reachable
- **Don't need to track** specific path
- **Greedy choice:** Always try to reach farthest possible

### Visualization:
```
nums = [2,3,1,1,4]
Index: 0 1 2 3 4

i=0: farthest = max(0, 0+2) = 2
i=1: farthest = max(2, 1+3) = 4 (>= 4, can reach end!)

nums = [3,2,1,0,4]  
Index: 0 1 2 3 4

i=0: farthest = max(0, 0+3) = 3
i=1: farthest = max(3, 1+2) = 3
i=2: farthest = max(3, 2+1) = 3
i=3: farthest = max(3, 3+0) = 3
i=4: i > farthest (4 > 3), return false
```

## Edge Cases:
1. **Single element** → Always true
2. **First element is 0** → Only true if array length is 1
3. **Array with zeros** → Check if zeros block the path
4. **Large jumps** → Can skip over zeros

## Follow-up Variations:

### Jump Game II:
- **Find minimum jumps** to reach end
- **Use BFS or greedy** to find shortest path

### Jump Game III:
- **Can jump both directions** (forward/backward)
- **Target any zero** instead of last index

## LeetCode Similar Problems:
- [55. Jump Game](https://leetcode.com/problems/jump-game/) (this problem)
- [45. Jump Game II](https://leetcode.com/problems/jump-game-ii/)
- [1306. Jump Game III](https://leetcode.com/problems/jump-game-iii/)
- [1345. Jump Game IV](https://leetcode.com/problems/jump-game-iv/)
- [1696. Jump Game VI](https://leetcode.com/problems/jump-game-vi/)

## Interview Tips:
- Start with DP approach to show understanding
- Optimize to greedy for better time/space complexity
- Explain why greedy works for this problem
- Handle edge cases like single element arrays
- This problem has both DP and greedy solutions 