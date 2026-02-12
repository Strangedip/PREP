# 4Sum (LeetCode 18)

## Problem Statement

Given an array `nums` of `n` integers, return an array of all the unique quadruplets `[nums[a], nums[b], nums[c], nums[d]]` such that:

- `0 <= a, b, c, d < n`
- `a`, `b`, `c`, and `d` are distinct
- `nums[a] + nums[b] + nums[c] + nums[d] == target`

You may return the answer in any order.

**Example 1:**
```
Input: nums = [1,0,-1,0,-2,2], target = 0
Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
```

**Example 2:**
```
Input: nums = [2,2,2,2,2], target = 8
Output: [[2,2,2,2]]
```

**Constraints:**
- `1 <= nums.length <= 200`
- `-10^9 <= nums[i] <= 10^9`
- `-10^9 <= target <= 10^9`

---

## Approach 1: Brute Force (4 Nested Loops)

**Time:** O(n⁴), **Space:** O(n) for the result set

### How It Works

Iterate through every possible combination of four indices and check if the sum equals the target. Use a Set to avoid duplicate quadruplets.

```java
public List<List<Integer>> fourSum(int[] nums, int target) {
    Set<List<Integer>> resultSet = new HashSet<>();
    int n = nums.length;
    Arrays.sort(nums); // Sort to make dedup easier
    
    for (int i = 0; i < n - 3; i++) {
        for (int j = i + 1; j < n - 2; j++) {
            for (int k = j + 1; k < n - 1; k++) {
                for (int l = k + 1; l < n; l++) {
                    long sum = (long) nums[i] + nums[j] + nums[k] + nums[l];
                    if (sum == target) {
                        resultSet.add(Arrays.asList(nums[i], nums[j], nums[k], nums[l]));
                    }
                }
            }
        }
    }
    return new ArrayList<>(resultSet);
}
```

### Why This Is Not Acceptable in an Interview

The O(n⁴) time complexity is far too slow for arrays with even a few hundred elements. This approach should only be mentioned briefly to demonstrate you understand the problem before presenting the optimal solution.

---

## Approach 2: Sort + Two Pointers (Optimal)

**Time:** O(n³), **Space:** O(1) extra (O(n) for sorting if not in-place)

### How It Works

This is a direct extension of the 3Sum pattern. The idea is:

1. **Sort the array** so we can use two-pointer technique and skip duplicates efficiently.
2. **Fix the first two numbers** using two nested loops (indices `i` and `j`).
3. **Use two pointers** (`left` and `right`) to find the remaining two numbers whose sum equals the remaining target.
4. **Skip duplicates at every level** to ensure uniqueness of quadruplets.

### Critical Detail: Integer Overflow

Because `nums[i]` can be up to `10^9` and we are summing four of them, the sum can exceed `Integer.MAX_VALUE` (approximately `2.1 × 10^9`). You MUST use `long` for the sum computation. This is a very common mistake that costs candidates in interviews.

### Complete Implementation

```java
import java.util.*;

public class FourSum {
    
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        
        if (n < 4) return result;
        
        Arrays.sort(nums);
        
        for (int i = 0; i < n - 3; i++) {
            // Skip duplicates for the first number
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            // Early termination: if the smallest possible sum exceeds target
            long minSum = (long) nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3];
            if (minSum > target) break;
            
            // Early termination: if the largest possible sum with nums[i] is below target
            long maxSum = (long) nums[i] + nums[n - 3] + nums[n - 2] + nums[n - 1];
            if (maxSum < target) continue;
            
            for (int j = i + 1; j < n - 2; j++) {
                // Skip duplicates for the second number
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                
                // Early termination within inner loop
                long innerMinSum = (long) nums[i] + nums[j] + nums[j + 1] + nums[j + 2];
                if (innerMinSum > target) break;
                
                long innerMaxSum = (long) nums[i] + nums[j] + nums[n - 2] + nums[n - 1];
                if (innerMaxSum < target) continue;
                
                int left = j + 1;
                int right = n - 1;
                
                while (left < right) {
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];
                    
                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        
                        // Skip duplicates for the third number
                        while (left < right && nums[left] == nums[left + 1]) left++;
                        // Skip duplicates for the fourth number
                        while (left < right && nums[right] == nums[right - 1]) right--;
                        
                        left++;
                        right--;
                    } else if (sum < target) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }
        
        return result;
    }
}
```

### Dry Run Example

```
Input: nums = [1, 0, -1, 0, -2, 2], target = 0

Step 1: Sort → [-2, -1, 0, 0, 1, 2]

i=0 (nums[i]=-2):
  j=1 (nums[j]=-1):
    left=2, right=5: sum = -2 + -1 + 0 + 2 = -1 < 0 → left++
    left=3, right=5: sum = -2 + -1 + 0 + 2 = -1 < 0 → left++
    left=4, right=5: sum = -2 + -1 + 1 + 2 = 0 ✓ → Add [-2,-1,1,2]
  j=2 (nums[j]=0):
    left=3, right=5: sum = -2 + 0 + 0 + 2 = 0 ✓ → Add [-2,0,0,2]
    left=4, right=4: left >= right, stop
  j=3 (nums[j]=0): SKIP (duplicate of j=2)

i=1 (nums[i]=-1):
  j=2 (nums[j]=0):
    left=3, right=5: sum = -1 + 0 + 0 + 2 = 1 > 0 → right--
    left=3, right=4: sum = -1 + 0 + 0 + 1 = 0 ✓ → Add [-1,0,0,1]
  j=3 (nums[j]=0): SKIP (duplicate of j=2)

Result: [[-2,-1,1,2], [-2,0,0,2], [-1,0,0,1]]
```

### Why the Early Termination Pruning Matters

The early termination checks (`minSum > target` and `maxSum < target`) are not required for correctness but dramatically improve performance. For an array of 200 elements, these checks can reduce the actual number of operations by 10x or more in practice, even though the worst-case complexity remains O(n³).

---

## Approach 3: HashMap-Based (Alternative O(n³))

**Time:** O(n³) average, O(n⁴) worst case, **Space:** O(n²)

### How It Works

1. Precompute all pairwise sums and store them in a HashMap.
2. For each pair `(i, j)`, look up `target - nums[i] - nums[j]` in the HashMap.
3. Filter to ensure all four indices are distinct.

This approach is theoretically interesting but harder to implement correctly due to index collision handling and duplicate management. The Sort + Two Pointers approach is preferred in interviews.

---

## Generalization: kSum Problem

The 4Sum pattern generalizes to kSum. A Lead Engineer should understand this recursive decomposition:

```java
public List<List<Integer>> kSum(int[] nums, long target, int k, int start) {
    List<List<Integer>> result = new ArrayList<>();
    
    if (start >= nums.length) return result;
    
    // Base case: 2Sum with two pointers
    if (k == 2) {
        int left = start, right = nums.length - 1;
        while (left < right) {
            long sum = (long) nums[left] + nums[right];
            if (sum == target) {
                result.add(new ArrayList<>(Arrays.asList(nums[left], nums[right])));
                while (left < right && nums[left] == nums[left + 1]) left++;
                while (left < right && nums[right] == nums[right - 1]) right--;
                left++;
                right--;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return result;
    }
    
    // Recursive case: fix one number, reduce to (k-1)Sum
    for (int i = start; i < nums.length - k + 1; i++) {
        if (i > start && nums[i] == nums[i - 1]) continue;
        
        // Early termination
        long minSum = 0;
        for (int j = i; j < i + k; j++) minSum += nums[j];
        if (minSum > target) break;
        
        long maxSum = 0;
        for (int j = nums.length - k; j < nums.length; j++) maxSum += nums[j];
        maxSum += nums[i] - nums[nums.length - k]; // adjust
        
        for (List<Integer> subset : kSum(nums, target - nums[i], k - 1, i + 1)) {
            List<Integer> quad = new ArrayList<>();
            quad.add(nums[i]);
            quad.addAll(subset);
            result.add(quad);
        }
    }
    
    return result;
}
```

---

## Approach Comparison

| Approach | Time | Space | Handles Duplicates | Overflow Safe |
|----------|------|-------|-------------------|---------------|
| Brute Force | O(n⁴) | O(n) | With Set | Need long |
| Sort + Two Pointers | O(n³) | O(1) | With skip logic | Need long |
| HashMap Pairs | O(n³) avg | O(n²) | Complex | Need long |
| Generalized kSum | O(n^(k-1)) | O(k) | With skip logic | Need long |

---

## Common Mistakes

1. **Integer overflow**: Summing four integers each up to 10^9 overflows `int`. Always cast to `long`.
2. **Incomplete duplicate skipping**: Must skip duplicates at EVERY level (i, j, left, right).
3. **Off-by-one in duplicate skipping**: `if (j > i + 1 && ...)` not `if (j > 0 && ...)` — the second number starts after `i`, not at index 0.
4. **Missing early termination**: Not a correctness issue, but interviewers expect you to optimize.
5. **Forgetting edge cases**: Array with fewer than 4 elements, all elements the same.

---

## Edge Cases

| Case | Input | Expected Output |
|------|-------|----------------|
| Less than 4 elements | `[1, 2, 3]`, target=6 | `[]` |
| All same elements, valid | `[2,2,2,2]`, target=8 | `[[2,2,2,2]]` |
| All same elements, invalid | `[2,2,2,2]`, target=9 | `[]` |
| Large negative numbers | `[-10^9, -10^9, 10^9, 10^9]`, target=0 | `[[-10^9, -10^9, 10^9, 10^9]]` |
| No valid quadruplet | `[1,2,3,4]`, target=100 | `[]` |

---

## LeetCode Similar Problems

- [1. Two Sum](https://leetcode.com/problems/two-sum/)
- [15. 3Sum](https://leetcode.com/problems/3sum/)
- [16. 3Sum Closest](https://leetcode.com/problems/3sum-closest/)
- [454. 4Sum II](https://leetcode.com/problems/4sum-ii/) (HashMap approach for two arrays)
- [18. 4Sum](https://leetcode.com/problems/4sum/)

---

## Interview Tips

1. **Start by mentioning the pattern**: "This is an extension of 2Sum → 3Sum → 4Sum. The technique of fixing elements and reducing the problem generalizes to kSum."
2. **Sort first**: Always sort the array. This enables two-pointer technique and efficient duplicate skipping.
3. **Cast to long immediately**: State upfront that you will use `long` for the sum to prevent overflow. This shows attention to detail.
4. **Discuss pruning**: The early termination checks show optimization awareness that interviewers value at the Lead level.
5. **Mention the generalization**: Discussing the recursive kSum decomposition demonstrates depth of understanding beyond just solving the specific problem.
6. **Time complexity justification**: O(n³) is the best possible for 4Sum because the output can be O(n³) in the worst case (when the array has many valid quadruplets).
