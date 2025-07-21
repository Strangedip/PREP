import java.util.*;

/**
 * Problem: Best Time to Buy and Sell Stock
 * 
 * You are given an array prices where prices[i] is the price of a given stock on the ith day.
 * You want to maximize your profit by choosing a single day to buy one stock and choosing
 * a different day in the future to sell that stock.
 * 
 * Return the maximum profit you can achieve from this transaction. 
 * If you cannot achieve any profit, return 0.
 * 
 * Example:
 * Input: prices = [7,1,5,3,6,4]
 * Output: 5
 * Explanation: Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6-1 = 5.
 */
public class BestTimeToBuyAndSellStock {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * For each day, check all future days to find maximum profit.
     */
    public int maxProfitBruteForce(int[] prices) {
        int maxProfit = 0;
        
        // Try buying on each day
        for (int i = 0; i < prices.length - 1; i++) {
            // Try selling on each future day
            for (int j = i + 1; j < prices.length; j++) {
                int profit = prices[j] - prices[i];
                maxProfit = Math.max(maxProfit, profit);
            }
        }
        
        return maxProfit;
    }
    
    /**
     * APPROACH 2: TRACK MINIMUM PRICE (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Keep track of minimum price seen so far and maximum profit achievable.
     * The key insight: for each day, calculate profit if we sell today
     * after buying on the cheapest day seen so far.
     */
    public int maxProfitOptimal(int[] prices) {
        if (prices.length <= 1) return 0;
        
        int minPrice = prices[0];    // Minimum price seen so far
        int maxProfit = 0;           // Maximum profit achievable
        
        for (int i = 1; i < prices.length; i++) {
            // Calculate profit if we sell today
            int currentProfit = prices[i] - minPrice;
            
            // Update maximum profit
            maxProfit = Math.max(maxProfit, currentProfit);
            
            // Update minimum price for future calculations
            minPrice = Math.min(minPrice, prices[i]);
        }
        
        return maxProfit;
    }
    
    /**
     * APPROACH 3: DYNAMIC PROGRAMMING PERSPECTIVE
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Think of it as: at each day, what's the maximum profit we can make?
     * We track two states: holding stock vs not holding stock.
     */
    public int maxProfitDP(int[] prices) {
        if (prices.length <= 1) return 0;
        
        // State variables
        int buy = -prices[0];  // Max profit when we own stock (negative because we spent money)
        int sell = 0;          // Max profit when we don't own stock
        
        for (int i = 1; i < prices.length; i++) {
            // If we buy today: profit = 0 - prices[i] (we can only buy once)
            // If we keep previous buy state: profit = buy
            buy = Math.max(buy, -prices[i]);
            
            // If we sell today: profit = buy + prices[i]
            // If we keep previous sell state: profit = sell
            sell = Math.max(sell, buy + prices[i]);
        }
        
        return sell; // We want to end without holding stock
    }
    
    /**
     * APPROACH 4: KADANE'S ALGORITHM VARIANT
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Transform the problem: instead of prices, look at daily price differences.
     * Find maximum sum of contiguous subarray of differences.
     */
    public int maxProfitKadane(int[] prices) {
        if (prices.length <= 1) return 0;
        
        int maxProfit = 0;
        int currentProfit = 0;
        
        // Look at price differences day by day
        for (int i = 1; i < prices.length; i++) {
            int dailyChange = prices[i] - prices[i - 1];
            
            // Apply Kadane's algorithm on price differences
            currentProfit = Math.max(0, currentProfit + dailyChange);
            maxProfit = Math.max(maxProfit, currentProfit);
        }
        
        return maxProfit;
    }
    
    /**
     * APPROACH 5: WITH BUY/SELL DAYS TRACKING
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Modified optimal solution that also tracks which days to buy and sell.
     */
    public int[] maxProfitWithDays(int[] prices) {
        if (prices.length <= 1) return new int[]{0, -1, -1};
        
        int minPrice = prices[0];
        int maxProfit = 0;
        int buyDay = 0, sellDay = 0;
        int tempBuyDay = 0;
        
        for (int i = 1; i < prices.length; i++) {
            int currentProfit = prices[i] - minPrice;
            
            if (currentProfit > maxProfit) {
                maxProfit = currentProfit;
                buyDay = tempBuyDay;
                sellDay = i;
            }
            
            if (prices[i] < minPrice) {
                minPrice = prices[i];
                tempBuyDay = i;
            }
        }
        
        return new int[]{maxProfit, buyDay, sellDay};
    }
    
    /**
     * APPROACH 6: RECURSIVE SOLUTION (FOR UNDERSTANDING)
     * Time Complexity: O(2^n) - Exponential (very inefficient)
     * Space Complexity: O(n) - Recursion stack
     * 
     * This is just for educational purposes to show recursive thinking.
     */
    public int maxProfitRecursive(int[] prices) {
        return maxProfitRecursiveHelper(prices, 0, false);
    }
    
    private int maxProfitRecursiveHelper(int[] prices, int day, boolean holding) {
        // Base case: no more days
        if (day >= prices.length) {
            return 0;
        }
        
        if (holding) {
            // We own stock: either sell today or hold
            int sellToday = prices[day]; // Sell and get profit
            int holdStock = maxProfitRecursiveHelper(prices, day + 1, true);
            return Math.max(sellToday, holdStock);
        } else {
            // We don't own stock: either buy today or skip
            int buyToday = -prices[day] + maxProfitRecursiveHelper(prices, day + 1, true);
            int skipToday = maxProfitRecursiveHelper(prices, day + 1, false);
            return Math.max(buyToday, skipToday);
        }
    }
    
    /**
     * APPROACH 7: HANDLING EDGE CASES
     * Comprehensive solution with all edge case handling.
     */
    public int maxProfitRobust(int[] prices) {
        // Edge cases
        if (prices == null || prices.length <= 1) {
            return 0;
        }
        
        // Check for all same prices
        boolean allSame = true;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] != prices[0]) {
                allSame = false;
                break;
            }
        }
        if (allSame) return 0;
        
        // Check for strictly decreasing prices
        boolean decreasing = true;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] >= prices[i - 1]) {
                decreasing = false;
                break;
            }
        }
        if (decreasing) return 0;
        
        // Use optimal solution
        return maxProfitOptimal(prices);
    }
    
    // Test method
    public static void main(String[] args) {
        BestTimeToBuyAndSellStock solution = new BestTimeToBuyAndSellStock();
        
        // Test case 1: Normal case
        int[] prices1 = {7, 1, 5, 3, 6, 4};
        System.out.println("Test Case 1: prices = [7,1,5,3,6,4]");
        System.out.println("Brute Force: " + solution.maxProfitBruteForce(prices1));
        System.out.println("Optimal: " + solution.maxProfitOptimal(prices1));
        System.out.println("DP: " + solution.maxProfitDP(prices1));
        System.out.println("Kadane's: " + solution.maxProfitKadane(prices1));
        
        int[] result = solution.maxProfitWithDays(prices1);
        System.out.println("Max profit: " + result[0] + ", Buy day: " + result[1] + ", Sell day: " + result[2]);
        System.out.println();
        
        // Test case 2: Decreasing prices
        int[] prices2 = {7, 6, 4, 3, 1};
        System.out.println("Test Case 2: prices = [7,6,4,3,1]");
        System.out.println("Optimal: " + solution.maxProfitOptimal(prices2));
        System.out.println();
        
        // Test case 3: Increasing prices
        int[] prices3 = {1, 2, 3, 4, 5};
        System.out.println("Test Case 3: prices = [1,2,3,4,5]");
        System.out.println("Optimal: " + solution.maxProfitOptimal(prices3));
        System.out.println();
        
        // Test case 4: Single element
        int[] prices4 = {5};
        System.out.println("Test Case 4: prices = [5]");
        System.out.println("Optimal: " + solution.maxProfitOptimal(prices4));
        System.out.println();
        
        // Test case 5: Two elements
        int[] prices5 = {2, 4};
        System.out.println("Test Case 5: prices = [2,4]");
        System.out.println("Optimal: " + solution.maxProfitOptimal(prices5));
        System.out.println();
        
        // Test case 6: Complex case
        int[] prices6 = {3, 2, 6, 5, 0, 3};
        System.out.println("Test Case 6: prices = [3,2,6,5,0,3]");
        System.out.println("Optimal: " + solution.maxProfitOptimal(prices6));
        int[] result6 = solution.maxProfitWithDays(prices6);
        System.out.println("Max profit: " + result6[0] + ", Buy day: " + result6[1] + ", Sell day: " + result6[2]);
    }
} 