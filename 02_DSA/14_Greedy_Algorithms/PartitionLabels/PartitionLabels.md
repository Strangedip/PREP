# Partition Labels

## Problem Statement
You are given a string s. We want to partition this string into as many parts as possible so that each letter appears in at most one part. Return a list of integers representing the size of these parts.

## Example
```
Input: s = "ababcbacadefegdehijhklij"
Output: [9,7,8]
Explanation:
The partition is "ababcbaca", "defegde", "hijhklij".
This is a partition so that each letter appears in at most one part.
```

## Approach: Greedy with Last Occurrence

### Key Insight:
**For each partition, we must include all characters up to the last occurrence of any character in the current partition.**

### How it works:
1. **Find last occurrence** of each character
2. **Track current partition end** based on characters seen so far
3. **When current position reaches partition end**, we can make a cut
4. **Extend partition end** when we see characters with later last occurrences

### Key Logic:
```java
public List<Integer> partitionLabels(String s) {
    // Step 1: Find last occurrence of each character
    int[] lastOccurrence = new int[26];
    for (int i = 0; i < s.length(); i++) {
        lastOccurrence[s.charAt(i) - 'a'] = i;
    }
    
    List<Integer> result = new ArrayList<>();
    int start = 0;
    int end = 0;
    
    // Step 2: Iterate and determine partition boundaries
    for (int i = 0; i < s.length(); i++) {
        char currentChar = s.charAt(i);
        
        // Extend current partition to include last occurrence of current char
        end = Math.max(end, lastOccurrence[currentChar - 'a']);
        
        // If we've reached the end of current partition
        if (i == end) {
            result.add(end - start + 1);
            start = i + 1;
        }
    }
    
    return result;
}
```

### Time & Space Complexity:
- **Time:** O(n) - Two passes through string
- **Space:** O(1) - Fixed size array for 26 letters

## Step-by-Step Example:
```
s = "ababcbacadefegdehijhklij"

Last occurrences:
a: 8, b: 5, c: 7, d: 14, e: 15, f: 11, g: 13, h: 19, i: 22, j: 23, k: 20, l: 21

Iteration:
i=0, char='a': end = max(0, 8) = 8
i=1, char='b': end = max(8, 5) = 8  
i=2, char='a': end = max(8, 8) = 8
i=3, char='b': end = max(8, 5) = 8
i=4, char='c': end = max(8, 7) = 8
i=5, char='b': end = max(8, 5) = 8
i=6, char='a': end = max(8, 8) = 8
i=7, char='c': end = max(8, 7) = 8
i=8, char='a': end = max(8, 8) = 8, i==end → partition size = 9

i=9, char='d': end = max(8, 14) = 14, start = 9
i=10, char='e': end = max(14, 15) = 15
...
i=15, char='e': end = max(15, 15) = 15, i==end → partition size = 7

i=16, char='h': end = max(15, 19) = 19, start = 16
...
i=23, char='j': end = max(23, 23) = 23, i==end → partition size = 8

Result: [9, 7, 8]
```

## Why This is Greedy:

### Greedy Choice:
**Make a partition cut as soon as possible** - when we've seen all characters up to their last occurrence.

### Optimality:
1. **Can't cut earlier** - would separate characters that should be together
2. **No benefit in cutting later** - would make partitions unnecessarily large
3. **Locally optimal choice** leads to globally optimal solution

## Visual Representation:
```
s = "ababcbacadefegdehijhklij"
     |------| |----| |------|
     Part 1   Part 2  Part 3

Part 1: "ababcbaca" - contains a,b,c and reaches their last occurrences
Part 2: "defegde" - contains d,e,f,g and reaches their last occurrences  
Part 3: "hijhklij" - contains h,i,j,k,l and reaches their last occurrences
```

## Alternative Implementation:

### Using HashMap:
```java
public List<Integer> partitionLabels(String s) {
    Map<Character, Integer> lastIndex = new HashMap<>();
    
    // Find last occurrence of each character
    for (int i = 0; i < s.length(); i++) {
        lastIndex.put(s.charAt(i), i);
    }
    
    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;
    
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, lastIndex.get(s.charAt(i)));
        
        if (i == end) {
            result.add(end - start + 1);
            start = i + 1;
        }
    }
    
    return result;
}
```

## Edge Cases:
1. **Single character** "a" → [1]
2. **All same characters** "aaaa" → [4]
3. **All different characters** "abcd" → [1,1,1,1]
4. **Already optimal** "abc" → [1,1,1]

## Pattern Recognition:

### This problem demonstrates:
- **Greedy interval merging** pattern
- **Last occurrence** tracking technique
- **Partition optimization** strategy

### Similar patterns appear in:
- **Merge intervals** problems
- **Activity selection** problems
- **Optimal scheduling** problems

## LeetCode Similar Problems:
- [763. Partition Labels](https://leetcode.com/problems/partition-labels/) (this problem)
- [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/)
- [435. Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/)
- [452. Minimum Number of Arrows to Burst Balloons](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/)

## Interview Tips:
- Start by identifying what determines partition boundaries
- Explain the greedy choice: cut as early as possible
- Walk through the algorithm with a concrete example
- Handle edge cases like single character or all same characters
- This showcases interval-based greedy thinking 