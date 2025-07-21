# Subsets (Power Set)

## Problem Statement
Given an integer array `nums` of **unique elements**, return all possible subsets (the power set).

The solution set must **not contain duplicate subsets**. Return the solution in any order.

**Examples:**
```
Input: nums = [1,2,3]
Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]

Input: nums = [0]
Output: [[],[0]]

Input: nums = []
Output: [[]]
```

## Problem Analysis

### Core Insight
The **power set** of a set with `n` elements has exactly **2ⁿ subsets** because for each element, we have **2 choices**: include it or exclude it.

### Mathematical Foundation
- **Empty set**: ∅ (always included)
- **Total subsets**: 2ⁿ where n = |nums|
- **Subset enumeration**: Can be mapped to binary numbers 0 to 2ⁿ-1

## Approaches

### Approach 1: Backtracking ⭐ (Most Fundamental)

#### Key Insight
**Build subsets incrementally** by making include/exclude decisions for each element.

#### Algorithm
```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    // Every recursive call represents a valid subset
    result.add(new ArrayList<>(current));
    
    // Try adding each remaining element
    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);              // Make choice
        backtrack(nums, i + 1, current, result);  // Recurse
        current.remove(current.size() - 1); // Backtrack
    }
}
```

#### Why This Works
1. **Every recursive call** adds current state to result (represents a valid subset)
2. **Start index** ensures we don't revisit earlier elements (avoids duplicates)
3. **Backtracking** explores all possible combinations systematically

#### Time Complexity
- **O(n × 2ⁿ)** - 2ⁿ subsets, each takes O(n) to copy

#### Space Complexity
- **O(n × 2ⁿ)** - storing all subsets + O(n) recursion stack

### Approach 2: Include/Exclude Backtracking

#### Key Insight
**Explicitly model binary choice** for each element: include or exclude.

```java
private void backtrackIncludeExclude(int[] nums, int index, List<Integer> current, List<List<Integer>> result) {
    // Base case: processed all elements
    if (index == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    
    // Choice 1: Exclude current element
    backtrackIncludeExclude(nums, index + 1, current, result);
    
    // Choice 2: Include current element
    current.add(nums[index]);
    backtrackIncludeExclude(nums, index + 1, current, result);
    current.remove(current.size() - 1); // Backtrack
}
```

**Decision Tree Structure**: Each level represents one element, two branches per level.

### Approach 3: Bit Manipulation ⭐ (Most Efficient)

#### Key Insight
**Map each subset to a binary number** where bit `i` indicates whether `nums[i]` is included.

#### Algorithm
```java
public List<List<Integer>> subsetsBitManipulation(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    int n = nums.length;
    
    // Iterate through all possible bit masks (0 to 2^n - 1)
    for (int mask = 0; mask < (1 << n); mask++) {
        List<Integer> subset = new ArrayList<>();
        
        // Check each bit position
        for (int i = 0; i < n; i++) {
            // If bit i is set, include nums[i]
            if ((mask & (1 << i)) != 0) {
                subset.add(nums[i]);
            }
        }
        
        result.add(subset);
    }
    
    return result;
}
```

#### Bit Mapping Example
For `nums = [1,2,3]`:
```
Mask 0 (000): [] 
Mask 1 (001): [1]
Mask 2 (010): [2]  
Mask 3 (011): [1,2]
Mask 4 (100): [3]
Mask 5 (101): [1,3]
Mask 6 (110): [2,3]
Mask 7 (111): [1,2,3]
```

#### Advantages
- **No recursion** (iterative)
- **Direct mapping** between numbers and subsets
- **Space efficient** (no recursion stack)

### Approach 4: Iterative Building

#### Key Insight
**Start with empty set**, then for each new element, **double the subsets** by creating copies with the new element added.

```java
public List<List<Integer>> subsetsIterative(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>()); // Start with empty subset
    
    for (int num : nums) {
        int size = result.size();
        
        // For each existing subset, create new subset with current element
        for (int i = 0; i < size; i++) {
            List<Integer> newSubset = new ArrayList<>(result.get(i));
            newSubset.add(num);
            result.add(newSubset);
        }
    }
    
    return result;
}
```

#### Step-by-step for [1,2,3]:
```
Initial: [[]]
Add 1:   [[], [1]]
Add 2:   [[], [1], [2], [1,2]]  
Add 3:   [[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]
```

## Example Trace (Backtracking)

**Input**: `nums = [1,2]`

**Recursion Tree**:
```
                backtrack(0, [])
                      |
                 Add [] to result
                 /            \
        Include 1              Skip 1
      /           \                 \
backtrack(1,[1])              backtrack(2,[])
     |                             |
Add [1] to result              Add [] already done
   /        \
Include 2   Skip 2  
   |           \
backtrack(2,[1,2])  backtrack(2,[1])
     |                    |
Add [1,2] to result  Add [1] already done
```

**Result**: `[[], [1], [1,2], [2]]`

## Handling Duplicates (Subsets II)

### Problem Extension
When input contains **duplicates**, avoid generating duplicate subsets.

### Solution Strategy
1. **Sort the array** to group duplicates together
2. **Skip duplicate elements** in backtracking using the pattern:
   ```java
   if (i > start && nums[i] == nums[i-1]) continue;
   ```

### Why This Works
- **Sorting** ensures duplicates are adjacent
- **Skip condition** `i > start` ensures we only skip duplicates at the same recursion level
- **First occurrence** of duplicate is always processed

```java
private void backtrackWithDup(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current));
    
    for (int i = start; i < nums.length; i++) {
        // Skip duplicates at same level
        if (i > start && nums[i] == nums[i - 1]) {
            continue;
        }
        
        current.add(nums[i]);
        backtrackWithDup(nums, i + 1, current, result);
        current.remove(current.size() - 1);
    }
}
```

## Comparison of Approaches

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Backtracking | O(n×2ⁿ) | O(n×2ⁿ) | Intuitive, standard pattern | Recursion overhead |
| Include/Exclude | O(n×2ⁿ) | O(n×2ⁿ) | Clear binary decision model | Deeper recursion |
| Bit Manipulation | O(n×2ⁿ) | O(n×2ⁿ) | No recursion, direct mapping | Less intuitive |
| Iterative | O(n×2ⁿ) | O(n×2ⁿ) | Simple doubling pattern | High memory usage |

## Key Backtracking Principles

### 1. **State Representation**
- **Current subset**: `List<Integer> current`
- **Position**: `int start` (which element to consider next)
- **Goal**: Add all valid states to result

### 2. **Decision Making**
- **For each position**: Include element or skip to next position
- **Avoid duplicates**: Use start index to prevent revisiting

### 3. **Result Collection**
- **Every recursive call** represents a valid subset
- **Copy the current state** before adding to result

### 4. **Backtracking Pattern**
```java
void backtrack(state) {
    addToResult(state);  // Process current state
    
    for (choice in validChoices) {
        makeChoice(choice);     // Modify state
        backtrack(newState);    // Recurse
        undoChoice(choice);     // Backtrack
    }
}
```

## Mathematical Insights

### Power Set Properties
- **Cardinality**: |P(S)| = 2^|S|
- **Empty set**: Always included (∅ ∈ P(S))
- **Subset relationships**: Every subset is related by ⊆

### Combinatorial Interpretation
Subsets are equivalent to:
- **Binary strings** of length n
- **Selections** from n items
- **Characteristic functions** on the set

## Extensions and Variations

### 1. **Subsets of Size K** (Combinations)
Generate only subsets with exactly k elements:
```java
if (current.size() == k) {
    result.add(new ArrayList<>(current));
    return;
}
```

### 2. **Subsets with Constraints**
- Sum equals target
- Contains specific elements
- Lexicographic ordering

### 3. **Multiset Subsets**
Handle elements with multiplicity (can appear multiple times).

## Interview Tips

1. **Start with backtracking** - most interviewers expect this approach
2. **Explain the binary choice model** - include/exclude for each element
3. **Discuss bit manipulation** - shows understanding of mathematical mapping
4. **Handle edge cases** - empty array, single element
5. **Mention optimizations** - early termination, duplicate handling

## Common Mistakes

1. **Forgetting to copy** current list when adding to result
2. **Not handling duplicates** properly (wrong skip condition)
3. **Wrong start index** in recursion (causes duplicate subsets)
4. **Bit manipulation errors** - wrong bit operations or bounds

The Subsets problem is **fundamental to combinatorial algorithms** and demonstrates key computer science concepts: recursion, backtracking, bit manipulation, and the mathematical connection between algorithms and combinatorics! 