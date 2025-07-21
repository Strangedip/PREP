# 4Sum

## Problem Statement
Find all unique quadruplets in array that sum to target.

## Approach 1: Brute Force
**Time:** O(n⁴), **Space:** O(1)

Check all possible combinations of 4 numbers.

## Approach 2: Sort + Two Pointers (Optimal!)
**Time:** O(n³), **Space:** O(1)

Extension of 3Sum pattern - fix two numbers, use two pointers for remaining two.

### Key Steps:
1. Sort array
2. Fix first two numbers with nested loops
3. Use two pointers for remaining sum
4. Skip duplicates at all levels

## Interview Strategy
"This extends the 3Sum pattern by adding another nested loop."

## Note
**For Mid-Level Interviews:** Master the O(n³) two pointers approach. Handle integer overflow with long casting. 