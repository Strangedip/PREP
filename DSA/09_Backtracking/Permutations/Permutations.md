# Permutations

## Problem Statement
Given an array `nums` of **distinct integers**, return all the possible permutations. You can return the answer in any order.

**Examples:**
```
Input: nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]

Input: nums = [0,1]
Output: [[0,1],[1,0]]

Input: nums = [1]
Output: [[1]]
```

## Problem Analysis

### Core Insight
A **permutation** is an arrangement of elements where **order matters**. For `n` distinct elements, there are exactly **n!** permutations because:
- First position: `n` choices
- Second position: `n-1` choices  
- Third position: `n-2` choices
- ...
- Last position: `1` choice

**Total**: `n × (n-1) × (n-2) × ... × 1 = n!`

### Mathematical Foundation
- **n! permutations** of n distinct elements
- **Each element appears in each position** exactly `(n-1)!` times
- **Lexicographic ordering** provides systematic enumeration

## Approaches

### Approach 1: Backtracking with Used Array ⭐ (Most Intuitive)

#### Key Insight
**Track which elements are already used** in the current permutation using a boolean array.

#### Algorithm
```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, used, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
    // Base case: permutation is complete
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    
    // Try each unused element
    for (int i = 0; i < nums.length; i++) {
        if (!used[i]) {
            used[i] = true;                    // Mark as used
            current.add(nums[i]);             // Make choice
            backtrack(nums, used, current, result);  // Recurse
            current.remove(current.size() - 1); // Backtrack
            used[i] = false;                   // Mark as unused
        }
    }
}
```

#### Why This Works
1. **Used array** prevents selecting the same element twice
2. **Backtracking** systematically explores all possibilities
3. **Base case** triggers when permutation reaches target length

#### Time Complexity
- **O(n! × n)** - n! permutations, each takes O(n) to copy

#### Space Complexity  
- **O(n! × n)** - storing all permutations + O(n) for recursion/used array

### Approach 2: Backtracking with Swapping ⭐ (More Efficient)

#### Key Insight
**Generate permutations in-place** by swapping elements instead of using extra space to track used elements.

#### Algorithm
```java
public List<List<Integer>> permuteSwapping(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrackSwap(nums, 0, result);
    return result;
}

private void backtrackSwap(int[] nums, int start, List<List<Integer>> result) {
    // Base case: all positions filled
    if (start == nums.length) {
        result.add(Arrays.stream(nums).boxed().collect(Collectors.toList()));
        return;
    }
    
    // Try placing each remaining element at current position
    for (int i = start; i < nums.length; i++) {
        swap(nums, start, i);              // Make choice
        backtrackSwap(nums, start + 1, result);  // Recurse
        swap(nums, start, i);              // Backtrack (undo)
    }
}
```

#### Key Insight Behind Swapping
- **Position `start`**: We're deciding what element goes here
- **Loop variable `i`**: Try each remaining element (from `start` to `end`)
- **Swap**: Move element at position `i` to position `start`
- **Backtrack**: Restore original order by swapping back

#### Advantages
- **Space efficient**: No extra `used` array needed
- **In-place**: Modifies input array directly
- **Same time complexity** but better space usage

### Approach 3: Iterative Building

#### Key Insight
**Build permutations incrementally** by inserting each new element at all possible positions in existing permutations.

#### Algorithm
```java
public List<List<Integer>> permuteIterative(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>()); // Start with empty permutation
    
    for (int num : nums) {
        List<List<Integer>> newPermutations = new ArrayList<>();
        
        for (List<Integer> permutation : result) {
            // Insert current number at each possible position
            for (int i = 0; i <= permutation.size(); i++) {
                List<Integer> newPermutation = new ArrayList<>(permutation);
                newPermutation.add(i, num);
                newPermutations.add(newPermutation);
            }
        }
        
        result = newPermutations;
    }
    
    return result;
}
```

#### Step-by-step for [1,2,3]:
```
Initial: [[]]
Add 1:   [[1]]
Add 2:   [[2,1], [1,2]]
Add 3:   [[3,2,1], [2,3,1], [2,1,3], [3,1,2], [1,3,2], [1,2,3]]
```

### Approach 4: Lexicographic Generation

#### Key Insight
**Generate permutations in lexicographic order** using the next permutation algorithm.

#### Algorithm
1. Start with sorted array (smallest permutation)
2. Repeatedly find next lexicographic permutation
3. Stop when no next permutation exists

```java
public List<List<Integer>> permuteLexicographic(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    Arrays.sort(nums);
    
    do {
        result.add(Arrays.stream(nums).boxed().collect(Collectors.toList()));
    } while (nextPermutation(nums));
    
    return result;
}
```

#### Next Permutation Algorithm
```java
private boolean nextPermutation(int[] nums) {
    // Step 1: Find largest i such that nums[i] < nums[i+1]
    int i = nums.length - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) i--;
    
    if (i == -1) return false; // No next permutation
    
    // Step 2: Find largest j such that nums[i] < nums[j]
    int j = nums.length - 1;
    while (nums[j] <= nums[i]) j--;
    
    // Step 3: Swap nums[i] and nums[j]
    swap(nums, i, j);
    
    // Step 4: Reverse suffix starting at i+1
    reverse(nums, i + 1, nums.length - 1);
    
    return true;
}
```

## Example Trace (Used Array Approach)

**Input**: `nums = [1,2]`

**Recursion Tree**:
```
                backtrack([], used=[F,F])
                         |
                    Try each element
                    /              \
              Use 1               Use 2
          /           \               \
backtrack([1], [T,F])          backtrack([2], [F,T])
         |                             |
    Try remaining                 Try remaining
         |                             |
    Use 2                         Use 1
         |                             |
backtrack([1,2], [T,T])        backtrack([2,1], [T,T])
         |                             |
    Add [1,2]                     Add [2,1]
```

**Result**: `[[1,2], [1,2], [2,1]]`

## Handling Duplicates (Permutations II)

### Problem Extension
When input contains **duplicates**, avoid generating duplicate permutations.

### Solution Strategy
1. **Sort the array** to group duplicates together
2. **Skip duplicates** using the pattern:
   ```java
   if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) continue;
   ```

### Why Skip Condition Works
- **`nums[i] == nums[i-1]`**: Found a duplicate
- **`!used[i-1]`**: Previous duplicate not used yet
- **Skip**: Ensures we use duplicates in order (left to right)

```java
private void backtrackUnique(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        
        // Skip duplicates: only use if previous same element is used
        if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) {
            continue;
        }
        
        used[i] = true;
        current.add(nums[i]);
        backtrackUnique(nums, used, current, result);
        current.remove(current.size() - 1);
        used[i] = false;
    }
}
```

## Comparison of Approaches

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Used Array | O(n!×n) | O(n!×n) | Most intuitive | Extra space for used array |
| Swapping | O(n!×n) | O(n!×n) | Space efficient | Modifies input array |
| Iterative | O(n!×n) | O(n!×n) | No recursion | High memory usage per step |
| Lexicographic | O(n!×n) | O(n!×n) | Ordered output | Complex next permutation logic |

## Key Backtracking Principles

### 1. **Decision Space**
At each step: **Which unused element to place next?**

### 2. **State Representation**
- **Current permutation**: `List<Integer> current`
- **Used elements**: `boolean[] used` or position-based logic
- **Goal**: Permutation of length n

### 3. **Constraint Checking**
- **Feasibility**: Element not already used
- **Completeness**: All elements placed

### 4. **Backtracking Template**
```java
void backtrack(state) {
    if (isComplete(state)) {
        addToResult(state);
        return;
    }
    
    for (choice in getValidChoices(state)) {
        makeChoice(choice);
        backtrack(newState);
        undoChoice(choice);
    }
}
```

## Mathematical Insights

### Factorial Growth
```
n=1: 1! = 1
n=2: 2! = 2  
n=3: 3! = 6
n=4: 4! = 24
n=5: 5! = 120
n=10: 10! = 3,628,800
```

**Growth rate**: Factorial growth is **faster than exponential**!

### Permutations with Repetition
For array with duplicates: **n! / (n₁! × n₂! × ... × nₖ!)**
where `nᵢ` is frequency of element `i`.

## Extensions and Variations

### 1. **k-Permutations**
Generate permutations of length k (not necessarily using all elements).

### 2. **Circular Permutations**
Arrangements in a circle: **(n-1)!** distinct permutations.

### 3. **String Permutations**
Apply same algorithms to character arrays.

### 4. **Next/Previous Permutation**
Find lexicographically next or previous permutation.

## Interview Tips

1. **Start with used array approach** - most intuitive for explanations
2. **Explain the choice-making process** - what element to place next
3. **Discuss swapping optimization** - shows space awareness
4. **Handle duplicates carefully** - explain the skip condition logic
5. **Mention factorial time complexity** - shows understanding of growth

## Common Mistakes

1. **Forgetting to copy** current list when adding to result
2. **Wrong duplicate skip condition** - using `i > 0` vs `i > start`
3. **Not restoring state** in swapping approach
4. **Off-by-one errors** in next permutation algorithm

The Permutations problem is **fundamental to understanding backtracking** and demonstrates how algorithmic choices (used array vs swapping) can optimize space usage while maintaining the same core logic! 