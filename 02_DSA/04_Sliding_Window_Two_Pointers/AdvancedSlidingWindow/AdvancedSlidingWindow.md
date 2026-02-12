# Advanced Sliding Window Patterns

## Problem Statement
Advanced sliding window techniques that go beyond basic fixed-size windows, essential for solving complex array/string problems efficiently.

**Key Patterns:**
- **Variable Size Windows**
- **Multiple Sliding Windows**
- **Sliding Window with Data Structures**
- **Two Pointers with Complex Logic**
- **Sliding Window Maximum/Minimum**

## Pattern 1: Variable Size Sliding Window

### Template for "At Most K" Problems:
```java
public int atMostK(int[] nums, int k) {
    int left = 0, result = 0;
    Map<Integer, Integer> count = new HashMap<>();
    
    for (int right = 0; right < nums.length; right++) {
        // Expand window
        count.put(nums[right], count.getOrDefault(nums[right], 0) + 1);
        
        // Contract window while condition violated
        while (count.size() > k) {
            count.put(nums[left], count.get(nums[left]) - 1);
            if (count.get(nums[left]) == 0) {
                count.remove(nums[left]);
            }
            left++;
        }
        
        // Update result
        result += right - left + 1;
    }
    
    return result;
}

// Convert "exactly K" to "at most K" - "at most K-1"
public int exactlyK(int[] nums, int k) {
    return atMostK(nums, k) - atMostK(nums, k - 1);
}
```

### Longest Substring with K Distinct Characters:
```java
public int lengthOfLongestSubstringKDistinct(String s, int k) {
    if (k == 0) return 0;
    
    int left = 0, maxLength = 0;
    Map<Character, Integer> charCount = new HashMap<>();
    
    for (int right = 0; right < s.length(); right++) {
        char rightChar = s.charAt(right);
        charCount.put(rightChar, charCount.getOrDefault(rightChar, 0) + 1);
        
        // Contract window if more than k distinct characters
        while (charCount.size() > k) {
            char leftChar = s.charAt(left);
            charCount.put(leftChar, charCount.get(leftChar) - 1);
            if (charCount.get(leftChar) == 0) {
                charCount.remove(leftChar);
            }
            left++;
        }
        
        maxLength = Math.max(maxLength, right - left + 1);
    }
    
    return maxLength;
}
```

### Subarray with Sum Equals K:
```java
public int subarraySum(int[] nums, int k) {
    Map<Integer, Integer> prefixSumCount = new HashMap<>();
    prefixSumCount.put(0, 1); // Empty subarray has sum 0
    
    int count = 0, prefixSum = 0;
    
    for (int num : nums) {
        prefixSum += num;
        
        // Check if (prefixSum - k) exists
        count += prefixSumCount.getOrDefault(prefixSum - k, 0);
        
        // Update prefix sum count
        prefixSumCount.put(prefixSum, prefixSumCount.getOrDefault(prefixSum, 0) + 1);
    }
    
    return count;
}
```

## Pattern 2: Sliding Window Maximum/Minimum

### Using Deque for Sliding Window Maximum:
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>(); // Store indices
    
    for (int i = 0; i < n; i++) {
        // Remove elements outside current window
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
            deque.pollFirst();
        }
        
        // Remove elements smaller than current (they can't be maximum)
        while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        // Window is fully formed
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

### Sliding Window Minimum with Stack:
```java
public int[] slidingWindowMinimum(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>();
    
    for (int i = 0; i < n; i++) {
        // Remove elements outside window
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
            deque.pollFirst();
        }
        
        // Maintain increasing order (for minimum)
        while (!deque.isEmpty() && nums[deque.peekLast()] >= nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

## Pattern 3: Multiple Sliding Windows

### Finding All Anagrams:
```java
public List<Integer> findAnagrams(String s, String p) {
    List<Integer> result = new ArrayList<>();
    if (s.length() < p.length()) return result;
    
    int[] pCount = new int[26];
    int[] windowCount = new int[26];
    
    // Initialize pattern count and first window
    for (int i = 0; i < p.length(); i++) {
        pCount[p.charAt(i) - 'a']++;
        windowCount[s.charAt(i) - 'a']++;
    }
    
    if (Arrays.equals(pCount, windowCount)) {
        result.add(0);
    }
    
    // Slide the window
    for (int i = p.length(); i < s.length(); i++) {
        // Add new character
        windowCount[s.charAt(i) - 'a']++;
        
        // Remove old character
        windowCount[s.charAt(i - p.length()) - 'a']--;
        
        if (Arrays.equals(pCount, windowCount)) {
            result.add(i - p.length() + 1);
        }
    }
    
    return result;
}
```

### Minimum Window Substring:
```java
public String minWindow(String s, String t) {
    if (s.length() < t.length()) return "";
    
    Map<Character, Integer> targetCount = new HashMap<>();
    for (char c : t.toCharArray()) {
        targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
    }
    
    int left = 0, minLen = Integer.MAX_VALUE, minStart = 0;
    int required = targetCount.size();
    int formed = 0;
    
    Map<Character, Integer> windowCount = new HashMap<>();
    
    for (int right = 0; right < s.length(); right++) {
        char rightChar = s.charAt(right);
        windowCount.put(rightChar, windowCount.getOrDefault(rightChar, 0) + 1);
        
        if (targetCount.containsKey(rightChar) && 
            windowCount.get(rightChar).equals(targetCount.get(rightChar))) {
            formed++;
        }
        
        // Contract window
        while (formed == required && left <= right) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1;
                minStart = left;
            }
            
            char leftChar = s.charAt(left);
            windowCount.put(leftChar, windowCount.get(leftChar) - 1);
            
            if (targetCount.containsKey(leftChar) && 
                windowCount.get(leftChar) < targetCount.get(leftChar)) {
                formed--;
            }
            
            left++;
        }
    }
    
    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
```

## Pattern 4: Advanced Two Pointers

### Three Sum Closest:
```java
public int threeSumClosest(int[] nums, int target) {
    Arrays.sort(nums);
    int closestSum = nums[0] + nums[1] + nums[2];
    
    for (int i = 0; i < nums.length - 2; i++) {
        int left = i + 1, right = nums.length - 1;
        
        while (left < right) {
            int currentSum = nums[i] + nums[left] + nums[right];
            
            if (Math.abs(currentSum - target) < Math.abs(closestSum - target)) {
                closestSum = currentSum;
            }
            
            if (currentSum < target) {
                left++;
            } else if (currentSum > target) {
                right--;
            } else {
                return currentSum; // Exact match found
            }
        }
    }
    
    return closestSum;
}
```

### Container with Most Water:
```java
public int maxArea(int[] height) {
    int left = 0, right = height.length - 1;
    int maxWater = 0;
    
    while (left < right) {
        int width = right - left;
        int currentArea = Math.min(height[left], height[right]) * width;
        maxWater = Math.max(maxWater, currentArea);
        
        // Move pointer with smaller height
        if (height[left] < height[right]) {
            left++;
        } else {
            right--;
        }
    }
    
    return maxWater;
}
```

### Trapping Rain Water (Two Pointers):
```java
public int trap(int[] height) {
    int left = 0, right = height.length - 1;
    int leftMax = 0, rightMax = 0;
    int water = 0;
    
    while (left < right) {
        if (height[left] < height[right]) {
            if (height[left] >= leftMax) {
                leftMax = height[left];
            } else {
                water += leftMax - height[left];
            }
            left++;
        } else {
            if (height[right] >= rightMax) {
                rightMax = height[right];
            } else {
                water += rightMax - height[right];
            }
            right--;
        }
    }
    
    return water;
}
```

## Pattern 5: Sliding Window with Complex Data Structures

### Sliding Window Median:
```java
public double[] medianSlidingWindow(int[] nums, int k) {
    double[] result = new double[nums.length - k + 1];
    
    // Two heaps to maintain median
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    
    for (int i = 0; i < nums.length; i++) {
        // Add current element
        addNumber(nums[i], maxHeap, minHeap);
        
        // Remove element outside window
        if (i >= k) {
            removeNumber(nums[i - k], maxHeap, minHeap);
        }
        
        // Calculate median for complete window
        if (i >= k - 1) {
            result[i - k + 1] = getMedian(maxHeap, minHeap);
        }
    }
    
    return result;
}

private void addNumber(int num, PriorityQueue<Integer> maxHeap, 
                      PriorityQueue<Integer> minHeap) {
    if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
        maxHeap.offer(num);
    } else {
        minHeap.offer(num);
    }
    rebalance(maxHeap, minHeap);
}

private void removeNumber(int num, PriorityQueue<Integer> maxHeap, 
                         PriorityQueue<Integer> minHeap) {
    if (num <= maxHeap.peek()) {
        maxHeap.remove(num);
    } else {
        minHeap.remove(num);
    }
    rebalance(maxHeap, minHeap);
}

private void rebalance(PriorityQueue<Integer> maxHeap, 
                      PriorityQueue<Integer> minHeap) {
    if (maxHeap.size() > minHeap.size() + 1) {
        minHeap.offer(maxHeap.poll());
    } else if (minHeap.size() > maxHeap.size()) {
        maxHeap.offer(minHeap.poll());
    }
}

private double getMedian(PriorityQueue<Integer> maxHeap, 
                        PriorityQueue<Integer> minHeap) {
    if (maxHeap.size() == minHeap.size()) {
        return ((long) maxHeap.peek() + minHeap.peek()) / 2.0;
    } else {
        return maxHeap.peek();
    }
}
```

## Advanced Optimization Techniques:

### 1. Prefix Sum for Range Queries:
```java
// Precompute prefix sums for O(1) range sum queries
int[] prefixSum = new int[nums.length + 1];
for (int i = 0; i < nums.length; i++) {
    prefixSum[i + 1] = prefixSum[i] + nums[i];
}

// Range sum from i to j
int rangeSum = prefixSum[j + 1] - prefixSum[i];
```

### 2. Monotonic Stack/Deque:
```java
// Maintain monotonic property for efficient min/max queries
Deque<Integer> monotonicDeque = new ArrayDeque<>();
```

### 3. Rolling Hash for String Windows:
```java
// O(1) string comparison in sliding window
public class RollingHash {
    private final int MOD = 1000000007;
    private final int BASE = 256;
    
    public boolean areEqual(String s1, String s2) {
        return hash(s1) == hash(s2);
    }
    
    private long hash(String s) {
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = (hash * BASE + c) % MOD;
        }
        return hash;
    }
}
```

## LeetCode Similar Problems:
- [424. Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/)
- [992. Subarrays with K Different Integers](https://leetcode.com/problems/subarrays-with-k-different-integers/)
- [480. Sliding Window Median](https://leetcode.com/problems/sliding-window-median/)
- [1438. Longest Continuous Subarray With Absolute Diff Less Than or Equal to Limit](https://leetcode.com/problems/longest-continuous-subarray-with-absolute-diff-less-than-or-equal-to-limit/)
- [930. Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/)

## Interview Tips:
- **Master the variable window template** - it's extremely versatile
- **Use "at most K" technique** to solve "exactly K" problems
- **Know when to use deque** vs priority queue vs hash map
- **Practice multiple pointer problems** - they're becoming more common
- **These patterns are essential** for array/string problems at SDE2+ level 