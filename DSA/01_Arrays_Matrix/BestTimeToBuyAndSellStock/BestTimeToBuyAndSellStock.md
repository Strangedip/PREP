# Best Time to Buy and Sell Stock

## Problem Statement

You have an array representing stock prices on different days. Find the maximum profit you can make by buying on one day and selling on a later day. If no profit is possible, return 0.

**Constraints:**
- You can only buy once and sell once
- You must buy before you sell
- If no profit is possible, return 0

## Example
```
Input: prices = [7,1,5,3,6,4]
Output: 5
Explanation: Buy on day 2 (price=1), sell on day 5 (price=6), profit = 6-1 = 5
```

## Approach 1: Brute Force (Understanding the Problem)

### How it works:
Check every possible buy-sell combination to find the maximum profit.

1. For each day, consider buying on that day
2. For each future day, consider selling 
3. Calculate profit = sell_price - buy_price
4. Keep track of maximum profit

### Code Logic:
```java
for (int i = 0; i < prices.length - 1; i++) {      // Buy day
    for (int j = i + 1; j < prices.length; j++) {  // Sell day
        int profit = prices[j] - prices[i];
        maxProfit = Math.max(maxProfit, profit);
    }
}
```

### Complexity:
- **Time:** O(n²) - Check all pairs
- **Space:** O(1) 

### When to use:
- Only for tiny arrays (< 50 elements)
- To show problem understanding in interviews

## Approach 2: Track Minimum Price (Optimal!)

### The Big Idea (Simple Explanation):
Imagine you're a stock trader. As you see prices day by day, you want to remember the **cheapest price** you've seen so far. On each day, you ask yourself: "If I sell today, what's the maximum profit I could make?" (by buying on the cheapest day seen so far).

### Key Insight:
For maximum profit, you should buy at the **lowest price before** the selling day. So as you go through each day:
1. Calculate profit if you sell today (current_price - minimum_price_so_far)
2. Update your record of the minimum price
3. Keep track of the maximum profit seen

### Step-by-step Walkthrough:
```
prices = [7, 1, 5, 3, 6, 4]

Day 0: price=7, minPrice=7, profit=0, maxProfit=0
Day 1: price=1, minPrice=1, profit=0, maxProfit=0  
Day 2: price=5, minPrice=1, profit=4, maxProfit=4
Day 3: price=3, minPrice=1, profit=2, maxProfit=4
Day 4: price=6, minPrice=1, profit=5, maxProfit=5
Day 5: price=4, minPrice=1, profit=3, maxProfit=5

Result: 5
```

### Code Logic:
```java
int minPrice = prices[0];
int maxProfit = 0;

for (int i = 1; i < prices.length; i++) {
    // Profit if we sell today
    int currentProfit = prices[i] - minPrice;
    
    // Update maximum profit
    maxProfit = Math.max(maxProfit, currentProfit);
    
    // Update minimum price for future calculations
    minPrice = Math.min(minPrice, prices[i]);
}
```

### Complexity:
- **Time:** O(n) - Single pass through array
- **Space:** O(1) - Only two variables

### When to use:
- **This is the standard interview solution**
- All array sizes
- When you need optimal performance

## Approach 3: Dynamic Programming Perspective

### How it works:
Think of two states at each day:
- **Hold:** Maximum profit when you own stock
- **Sold:** Maximum profit when you don't own stock

### State Transitions:
```java
// At each day, we can:
buy = max(previous_buy, -current_price)     // Buy today or keep previous
sell = max(previous_sell, buy + current_price)  // Sell today or keep previous
```

### When to use:
- When you want to extend to multiple transactions
- Academic understanding of DP patterns
- Preparing for Stock II, III, IV variations

## Approach 4: Kadane's Algorithm Connection

### The Insight:
Transform the problem into finding maximum sum of contiguous subarray:
- Convert prices to daily changes: `[difference1, difference2, ...]`
- Find maximum sum subarray of these changes

### Example:
```
prices = [7, 1, 5, 3, 6, 4]
changes = [-6, 4, -2, 3, -2]
Max subarray sum = 4 + (-2) + 3 = 5
```

This represents buying after the -6 drop and selling after the +3 gain.

## Advanced Variations & Follow-ups

### Variation 1: Multiple Transactions
**"What if you could buy and sell multiple times?"**
- Solution: Sum all positive daily changes
- Pattern: Greedy approach

### Variation 2: With Transaction Fee
**"What if each transaction costs a fee?"**
- Solution: Modify DP to subtract fee on each transaction

### Variation 3: Cooldown Period
**"What if you can't buy immediately after selling?"**
- Solution: Add more states to DP

### Variation 4: At Most K Transactions
**"What if you can do at most K transactions?"**
- Solution: 2D DP with transaction count

## Edge Cases & Gotchas

### Case 1: Decreasing Prices
```
Input: [7, 6, 4, 3, 1]
Output: 0 (no profit possible)
```

### Case 2: Increasing Prices
```
Input: [1, 2, 3, 4, 5]
Output: 4 (buy first day, sell last day)
```

### Case 3: All Same Prices
```
Input: [3, 3, 3, 3]
Output: 0 (no profit possible)
```

### Case 4: Single Day
```
Input: [5]
Output: 0 (can't buy and sell same day)
```

### Case 5: Two Days
```
Input: [2, 1] → Output: 0
Input: [1, 2] → Output: 1
```

## Interview Strategy

### Step-by-step Approach:
1. **Clarify:** "I need to buy once and sell once, buying before selling, right?"
2. **Brute force:** "I could check all buy-sell pairs in O(n²)..."
3. **Optimize:** "But I can do better. For each day, I need the minimum price seen before it..."
4. **Code:** Implement the O(n) solution
5. **Test:** Walk through with given examples
6. **Follow-up:** Be ready for multiple transaction variations

### What Interviewers Look For:
- Recognition that we need minimum price before each selling day
- Clean implementation without off-by-one errors
- Correct handling of edge cases
- Understanding of the DP connection

## Common Coding Mistakes

1. **Starting loop from index 0:** Forgot that we need a price to compare against
2. **Wrong update order:** Updating minPrice before calculating profit
3. **Not handling single element:** Array bounds issues
4. **Negative profit handling:** Not using Math.max with 0

## Real-world Applications

1. **Trading Algorithms:** Actual stock trading systems
2. **Resource Allocation:** Buy resources when cheap, use when valuable
3. **Inventory Management:** Purchase timing optimization
4. **Cryptocurrency Trading:** Same pattern for crypto prices
5. **Commodity Trading:** Oil, gold, agricultural products

## Pattern Recognition

This problem teaches you to recognize:
- **State tracking patterns** (minimum so far, maximum profit so far)
- **DP state machines** (buy/sell states)
- **Greedy optimization** (always track the best option so far)

## Interview Variations You Might See

### Easy Variations:
- Return the buy and sell days (not just profit)
- Handle null/empty array cases

### Medium Variations:
- Best Time to Buy and Sell Stock II (multiple transactions)
- With transaction fees
- With cooldown period

### Hard Variations:
- At most K transactions
- Multiple stocks simultaneously

## Note

**For Mid-Level Interviews (2+ years):**
- **Master the O(n) solution:** Code it perfectly in under 5 minutes
- **Explain the intuition clearly:** Focus on "minimum price so far" concept
- **Know the complexity:** O(n) time, O(1) space
- **Handle edge cases:** Empty array, single element, no profit scenarios
- **Understand the DP connection:** Be ready for follow-up variations

**Red Flags to Avoid:**
- Suggesting brute force as final solution
- Not handling edge cases
- Off-by-one errors in array indexing
- Not explaining the "minimum price so far" insight

## LeetCode Similar Problems:
- [122. Best Time to Buy and Sell Stock II](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/)
- [123. Best Time to Buy and Sell Stock III](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/)
- [188. Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/)
- [309. Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/)
- [714. Best Time to Buy and Sell Stock with Transaction Fee](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/)

**Golden Pattern:** This problem establishes the "track optimal state so far" pattern that appears in many other problems. The technique of maintaining a running minimum/maximum while processing an array is fundamental!

**Remember:** This is often a warm-up question that leads to more complex stock problems. Master this first, then tackle the variations! 