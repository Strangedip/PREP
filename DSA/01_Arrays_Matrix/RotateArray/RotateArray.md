# Rotate Array

## Problem Statement
Rotate array to the right by k steps in-place.

## Approach 1: Extra Array
**Time:** O(n), **Space:** O(n)
Copy to new array at rotated positions.

## Approach 2: Reverse Algorithm (Optimal!)
**Time:** O(n), **Space:** O(1)

### The Trick:
1. Reverse entire array
2. Reverse first k elements  
3. Reverse remaining elements

Example: [1,2,3,4,5,6,7], k=3
- Reverse all: [7,6,5,4,3,2,1]
- Reverse first 3: [5,6,7,4,3,2,1] 
- Reverse rest: [5,6,7,1,2,3,4] ✓

## Note
**For Mid-Level:** Master the reverse trick - it's the expected O(1) space solution. 