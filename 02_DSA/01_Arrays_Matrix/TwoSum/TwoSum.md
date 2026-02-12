# Two Sum Problem

## Problem Statement

Given an array of integers and a target sum, find two numbers in the array that add up to the target. Return the **indices** (positions) of these two numbers.

**Important constraints:**
- Each input has exactly one solution
- You cannot use the same element twice
- You need to return the indices, not the values

## Example
```
Input: nums = [2, 7, 11, 15], target = 9
Output: [0, 1]
Explanation: nums[0] + nums[1] = 2 + 7 = 9
```

## Approach 1: Brute Force (Beginner Friendly)

### How it works (Simple explanation):
Think of this like checking every possible pair of friends to see if their ages add up to a specific number.

1. Pick the first person (index 0)
2. Check them with every other person after them
3. If their ages add up to target, we found our answer!
4. If not, pick the next person and repeat

### Code Logic:
```java
for (int i = 0; i < nums.length; i++) {
    for (int j = i + 1; j < nums.length; j++) {
        if (nums[i] + nums[j] == target) {
            return new int[]{i, j};
        }
    }
}
```

### Time & Space Complexity:
- **Time:** O(n²) - We check every possible pair
- **Space:** O(1) - We only use a few variables

### When to use:
- Small arrays (< 100 elements)
- When you want a simple, easy-to-understand solution
- During interviews, always mention this first to show you understand the problem

## Approach 2: HashMap Optimization (Interview Favorite!)

### How it works (Simple explanation):
Imagine you're at a party looking for someone whose age, when added to yours, equals a specific number. Instead of asking everyone their age repeatedly, you write down each person's age on a note as you meet them. When you meet someone new, you quickly check your notes to see if their "complement" (the age you need) is already written down.

### The Magic:
- For each number, calculate its **complement**: `complement = target - current_number`
- Check if this complement exists in our "memory" (HashMap)
- If yes, we found our pair!
- If no, remember the current number for future use

### Example Walkthrough:
```
nums = [2, 7, 11, 15], target = 9

Step 1: number = 2, complement = 9-2 = 7
        HashMap is empty, so add {2: 0}
        
Step 2: number = 7, complement = 9-7 = 2
        HashMap contains 2 at index 0!
        Return [0, 1]
```

### Code Logic:
```java
Map<Integer, Integer> map = new HashMap<>();
for (int i = 0; i < nums.length; i++) {
    int complement = target - nums[i];
    if (map.containsKey(complement)) {
        return new int[]{map.get(complement), i};
    }
    map.put(nums[i], i);
}
```

### Time & Space Complexity:
- **Time:** O(n) - We visit each element only once
- **Space:** O(n) - We store at most n elements in HashMap

### When to use:
- This is the **preferred interview solution**
- Large arrays
- When you need optimal time complexity

## Approach 3: Two Pointers (Bonus Approach)

### How it works:
Like two people walking towards each other from opposite ends of a sorted line. If their combined value is too small, the left person moves right. If too large, the right person moves left.

### Important Note:
This approach **changes the original indices** due to sorting, so we need to track original positions if indices are required.

### Time & Space Complexity:
- **Time:** O(n log n) due to sorting
- **Space:** O(1) for the two pointers, O(n) if we need to track original indices

### When to use:
- When you need to return values instead of indices
- When the array is already sorted
- For follow-up questions about space optimization

## Interview Tips

### What to say in an interview:
1. **Start with Brute Force:** "The simplest approach is to check every pair..."
2. **Identify the inefficiency:** "This is O(n²) because we're doing redundant work..."
3. **Propose optimization:** "We can use a HashMap to remember what we've seen..."
4. **Explain the trade-off:** "This improves time to O(n) but uses O(n) space..."

### Common Follow-up Questions:
1. **"What if the array is sorted?"** → Two pointers approach
2. **"What if we can't use extra space?"** → Discuss trade-offs
3. **"What if there are multiple solutions?"** → Modify to return all pairs
4. **"What if no solution exists?"** → Return empty array or throw exception

## Edge Cases to Consider

1. **Empty array:** `nums = []` → No solution possible
2. **Single element:** `nums = [5]` → No solution possible  
3. **Duplicate elements:** `nums = [3, 3], target = 6` → Should work fine
4. **Negative numbers:** `nums = [-1, -2, -3, -4, -5], target = -8` → All approaches work
5. **Zero in array:** `nums = [0, 4, 3, 0], target = 0` → Handle carefully

## Code Optimization Notes

### HashMap Approach Optimizations:
1. **Early return:** Check for complement before adding to map
2. **Input validation:** Check for null arrays and valid target
3. **Memory efficiency:** Use appropriate HashMap initial capacity

### Performance Considerations:
- HashMap approach is generally fastest for random data
- Two pointers is better for sorted data or when space is critical
- Brute force is acceptable for very small arrays (< 50 elements)

## Real-world Applications

1. **Financial systems:** Finding transactions that sum to a specific amount
2. **Inventory management:** Pairing items to reach target quantities
3. **Data analysis:** Finding correlated data points
4. **Gaming:** Matching player scores or levels

## Note

**For mid-level interviews (2+ years experience):**
- Always start with the optimal HashMap solution
- Mention time/space complexity immediately
- Discuss edge cases proactively
- Be ready to code it quickly and bug-free
- Know the trade-offs between different approaches

## LeetCode Similar Problems:
- [167. Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/)
- [15. 3Sum](https://leetcode.com/problems/3sum/)
- [18. 4Sum](https://leetcode.com/problems/4sum/)
- [454. 4Sum II](https://leetcode.com/problems/4sum-ii/)
- [653. Two Sum IV - BST](https://leetcode.com/problems/two-sum-iv-bst/)

**Remember:** This problem tests your ability to recognize patterns and optimize from O(n²) to O(n) using space as a trade-off. It's a fundamental pattern that appears in many other problems! 