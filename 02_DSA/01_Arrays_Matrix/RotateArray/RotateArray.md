# Rotate Array

## Problem Statement

Given an integer array `nums`, rotate the array to the right by `k` steps, where `k` is non-negative. You must do this **in-place** with O(1) extra space.

**LeetCode**: [189. Rotate Array](https://leetcode.com/problems/rotate-array/)

### Examples

```
Input:  nums = [1,2,3,4,5,6,7], k = 3
Output: [5,6,7,1,2,3,4]
Explanation:
  rotate 1 step: [7,1,2,3,4,5,6]
  rotate 2 step: [6,7,1,2,3,4,5]
  rotate 3 step: [5,6,7,1,2,3,4]

Input:  nums = [-1,-100,3,99], k = 2
Output: [3,99,-1,-100]
```

### Constraints
- `1 <= nums.length <= 10^5`
- `-2^31 <= nums[i] <= 2^31 - 1`
- `0 <= k <= 10^5`

---

## Approach 1: Extra Array (Brute Force)

**Time**: O(n), **Space**: O(n)

Copy each element to its new rotated position in a temporary array, then copy back.

```java
class Solution {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k = k % n; // Handle k > n

        int[] temp = new int[n];
        for (int i = 0; i < n; i++) {
            temp[(i + k) % n] = nums[i];
        }

        // Copy back
        System.arraycopy(temp, 0, nums, 0, n);
    }
}
```

> **Interview Note**: This approach works but uses O(n) space. The interviewer will ask you to do it in O(1) space.

---

## Approach 2: Reverse Algorithm (Optimal)

**Time**: O(n), **Space**: O(1)

This is the elegant solution. The key insight is that rotating right by k is equivalent to three reversal operations.

### The Three-Step Reverse Trick

1. **Reverse the entire array**
2. **Reverse the first k elements**
3. **Reverse the remaining n-k elements**

### Why This Works

Consider `[1, 2, 3, 4, 5, 6, 7]` with `k = 3`.

The rotated result should be `[5, 6, 7, 1, 2, 3, 4]`.

Notice that the result is two "blocks" rearranged: `[5,6,7]` (last k elements) followed by `[1,2,3,4]` (first n-k elements).

- **Step 1**: Reverse all → `[7, 6, 5, 4, 3, 2, 1]` — the blocks are in the right order now, but each block is internally reversed.
- **Step 2**: Reverse first k → `[5, 6, 7, 4, 3, 2, 1]` — first block is correct.
- **Step 3**: Reverse remaining → `[5, 6, 7, 1, 2, 3, 4]` — second block is correct.

### Visual Walkthrough

```
Original:     [1, 2, 3, 4, 5, 6, 7]    k = 3

Step 1: Reverse ALL
              [7, 6, 5, 4, 3, 2, 1]

Step 2: Reverse first k=3 elements
              [5, 6, 7, 4, 3, 2, 1]
               ^-----^

Step 3: Reverse remaining n-k=4 elements
              [5, 6, 7, 1, 2, 3, 4]
                        ^--------^

Result:       [5, 6, 7, 1, 2, 3, 4] ✓
```

### Java Implementation

```java
class Solution {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k = k % n; // Handle k >= n (rotating by n is same as rotating by 0)

        if (k == 0) return; // No rotation needed

        reverse(nums, 0, n - 1);     // Step 1: Reverse entire array
        reverse(nums, 0, k - 1);     // Step 2: Reverse first k elements
        reverse(nums, k, n - 1);     // Step 3: Reverse remaining elements
    }

    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }
}
```

---

## Approach 3: Cyclic Replacements

**Time**: O(n), **Space**: O(1)

Move each element to its final position directly using a cycle. When we place element at index `i` to index `(i + k) % n`, we displace the element that was already there. We continue this chain until we return to the starting index.

```java
class Solution {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k = k % n;

        if (k == 0) return;

        int count = 0; // Track how many elements we've moved

        for (int start = 0; count < n; start++) {
            int current = start;
            int prev = nums[start];

            do {
                int next = (current + k) % n;
                int temp = nums[next];
                nums[next] = prev;
                prev = temp;
                current = next;
                count++;
            } while (current != start);
        }
    }
}
```

### Why We Need Multiple Starting Points

If `n` and `k` share a common factor (GCD > 1), a single cycle will not visit all elements. For example, with `n = 6, k = 2`, we get:
- Cycle 1: `0 → 2 → 4 → 0` (only 3 elements)
- Cycle 2: `1 → 3 → 5 → 1` (remaining 3 elements)

The number of cycles equals `GCD(n, k)`, and each cycle visits `n / GCD(n, k)` elements.

---

## Complexity Analysis

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Extra Array | O(n) | O(n) | Simplest but not in-place |
| Three Reverses | O(n) | O(1) | Optimal — preferred in interviews |
| Cyclic Replacements | O(n) | O(1) | Optimal but harder to implement correctly |

Where n = length of the array. Each approach visits every element exactly once (or twice for the reverse approach).

---

## Edge Cases

| Case | Input | k | Output | Why It Matters |
|------|-------|---|--------|---------------|
| k = 0 | `[1,2,3]` | 0 | `[1,2,3]` | No rotation needed |
| k = n | `[1,2,3]` | 3 | `[1,2,3]` | Full rotation = no rotation |
| k > n | `[1,2,3]` | 5 | `[2,3,1]` | Must mod: k % n = 2 |
| Single element | `[1]` | 7 | `[1]` | Nothing to rotate |
| Two elements | `[1,2]` | 1 | `[2,1]` | Simple swap |
| All same | `[5,5,5]` | 2 | `[5,5,5]` | Result unchanged |
| k = 1 | `[1,2,3,4]` | 1 | `[4,1,2,3]` | Move last to front |

> **Critical Edge Case**: Always do `k = k % n` first. Without this, `k > n` will cause out-of-bounds access in the reverse approach.

---

## Interview Tips

1. **Start with brute force**: "I can use an extra array to compute rotated positions — O(n) time, O(n) space."
2. **Then optimize**: "But we can do O(1) space using the three-reverse trick."
3. **Explain the insight**: "Reversing all elements puts the two blocks in the right order but internally reversed. Two more reverses fix each block."
4. **Handle k >= n**: Always mention `k = k % n` — interviewers watch for this.
5. **Know the cyclic approach too**: If the interviewer asks for an alternative O(1) space solution, describe cyclic replacements and mention the GCD insight.

### Common Follow-Up Questions
- "Can you rotate LEFT by k steps?" → Use the same three-reverse trick but reverse first n-k, then last k, then all. Or equivalently, rotate right by n-k.
- "What if you have a linked list instead?" → This becomes [Rotate List](https://leetcode.com/problems/rotate-list/) — find the new tail, break and reattach.
- "Can you do this for a 2D matrix?" → Row-wise then column-wise reversal (related to matrix rotation).

---

## Related Problems

| Problem | Key Connection | LeetCode |
|---------|---------------|----------|
| Rotate List | Linked list version of rotate | [61](https://leetcode.com/problems/rotate-list/) |
| Reverse String | Same two-pointer reverse subroutine | [344](https://leetcode.com/problems/reverse-string/) |
| Rotate Image | 2D matrix rotation (transpose + reverse) | [48](https://leetcode.com/problems/rotate-image/) |
| Reverse Words in a String | Uses the same reverse-then-reverse trick | [151](https://leetcode.com/problems/reverse-words-in-a-string/) |

---

## Real-World Applications

- **Circular buffers**: Ring buffers in networking and OS kernels use the same modular arithmetic for wrapping indices.
- **Job scheduling**: Round-robin scheduling rotates tasks in a circular queue.
- **Cryptography**: Caesar cipher is essentially a character rotation.
- **Image processing**: Pixel rotation in image transformation pipelines.

---

**Pattern**: Reverse / Cyclic Replacement
**Difficulty**: Medium
**Must-Know**: Yes — the three-reverse trick appears in multiple problems
