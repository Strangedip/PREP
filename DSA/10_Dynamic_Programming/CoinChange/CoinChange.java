import java.util.*;

/**
 * LeetCode 322: Coin Change
 * 
 * You are given an integer array coins representing coins of different denominations
 * and an integer amount representing a total amount of money.
 * 
 * Return the fewest number of coins that you need to make up that amount.
 * If that amount of money cannot be made up by any combination of the coins, return -1.
 * 
 * You may assume that you have an infinite number of each kind of coin.
 * 
 * Time Complexity: O(amount * coins.length)
 * Space Complexity: O(amount)
 */
public class CoinChange {
    
    /**
     * Approach 1: Bottom-Up Dynamic Programming (Tabulation)
     * Build solution iteratively from smaller subproblems
     */
    public int coinChangeBottomUp(int[] coins, int amount) {
        // dp[i] = minimum coins needed to make amount i
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // Fill with impossible value
        
        dp[0] = 0; // Base case: 0 coins needed for amount 0
        
        // For each amount from 1 to target amount
        for (int i = 1; i <= amount; i++) {
            // Try each coin
            for (int coin : coins) {
                if (coin <= i) {
                    // If we can use this coin, update minimum
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        
        return dp[amount] > amount ? -1 : dp[amount];
    }
    
    /**
     * Approach 2: Top-Down Dynamic Programming (Memoization)
     * Use recursion with memoization to avoid recomputation
     */
    public int coinChangeTopDown(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2); // -2 means not computed, -1 means impossible
        
        int result = coinChangeHelper(coins, amount, memo);
        return result == Integer.MAX_VALUE ? -1 : result;
    }
    
    private int coinChangeHelper(int[] coins, int amount, int[] memo) {
        // Base cases
        if (amount < 0) return Integer.MAX_VALUE; // Invalid
        if (amount == 0) return 0; // No coins needed
        
        // Check if already computed
        if (memo[amount] != -2) {
            return memo[amount] == -1 ? Integer.MAX_VALUE : memo[amount];
        }
        
        int minCoins = Integer.MAX_VALUE;
        
        // Try each coin
        for (int coin : coins) {
            int subResult = coinChangeHelper(coins, amount - coin, memo);
            if (subResult != Integer.MAX_VALUE) {
                minCoins = Math.min(minCoins, subResult + 1);
            }
        }
        
        // Memoize result
        memo[amount] = (minCoins == Integer.MAX_VALUE) ? -1 : minCoins;
        return minCoins;
    }
    
    /**
     * Approach 3: BFS (Breadth-First Search)
     * Treat as shortest path problem in unweighted graph
     */
    public int coinChangeBFS(int[] coins, int amount) {
        if (amount == 0) return 0;
        
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        
        queue.offer(0);
        visited.add(0);
        
        int level = 0;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            level++;
            
            for (int i = 0; i < size; i++) {
                int current = queue.poll();
                
                for (int coin : coins) {
                    int next = current + coin;
                    
                    if (next == amount) {
                        return level;
                    }
                    
                    if (next < amount && !visited.contains(next)) {
                        visited.add(next);
                        queue.offer(next);
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Approach 4: Greedy with Backtracking (for specific coin systems)
     * Note: This only works for certain coin systems (like standard currency)
     */
    public int coinChangeGreedy(int[] coins, int amount) {
        // Sort coins in descending order
        Arrays.sort(coins);
        for (int i = 0; i < coins.length / 2; i++) {
            int temp = coins[i];
            coins[i] = coins[coins.length - 1 - i];
            coins[coins.length - 1 - i] = temp;
        }
        
        int[] result = {Integer.MAX_VALUE};
        greedyHelper(coins, amount, 0, 0, result);
        return result[0] == Integer.MAX_VALUE ? -1 : result[0];
    }
    
    private void greedyHelper(int[] coins, int amount, int coinIndex, int currentCoins, int[] result) {
        if (coinIndex >= coins.length) {
            if (amount == 0) {
                result[0] = Math.min(result[0], currentCoins);
            }
            return;
        }
        
        // Pruning: if current coins already exceed best result
        if (currentCoins >= result[0]) return;
        
        int coin = coins[coinIndex];
        int maxCount = amount / coin;
        
        // Try using 0 to maxCount of current coin
        for (int count = maxCount; count >= 0; count--) {
            greedyHelper(coins, amount - count * coin, coinIndex + 1, currentCoins + count, result);
        }
    }
    
    /**
     * Extension: Coin Change II - Count number of ways to make amount
     */
    public int coinChangeWays(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        dp[0] = 1; // One way to make amount 0
        
        // For each coin
        for (int coin : coins) {
            // Update all amounts that can use this coin
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
        }
        
        return dp[amount];
    }
    
    /**
     * Helper method to print the actual coins used
     */
    public List<Integer> getCoinCombination(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        int[] parent = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        Arrays.fill(parent, -1);
        
        dp[0] = 0;
        
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i && dp[i - coin] + 1 < dp[i]) {
                    dp[i] = dp[i - coin] + 1;
                    parent[i] = coin;
                }
            }
        }
        
        if (dp[amount] > amount) {
            return new ArrayList<>(); // No solution
        }
        
        // Reconstruct path
        List<Integer> result = new ArrayList<>();
        int current = amount;
        while (current > 0) {
            int coin = parent[current];
            result.add(coin);
            current -= coin;
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        CoinChange solution = new CoinChange();
        
        // Test case 1: Regular case
        int[] coins1 = {1, 3, 4};
        int amount1 = 6;
        System.out.println("Test case 1: coins = " + Arrays.toString(coins1) + ", amount = " + amount1);
        System.out.println("Bottom-up DP: " + solution.coinChangeBottomUp(coins1, amount1)); // 2 (3+3)
        System.out.println("Top-down DP: " + solution.coinChangeTopDown(coins1, amount1)); // 2
        System.out.println("BFS: " + solution.coinChangeBFS(coins1, amount1)); // 2
        System.out.println("Greedy: " + solution.coinChangeGreedy(coins1.clone(), amount1)); // 2
        System.out.println("Coin combination: " + solution.getCoinCombination(coins1, amount1));
        System.out.println("Number of ways: " + solution.coinChangeWays(coins1, amount1));
        System.out.println();
        
        // Test case 2: No solution
        int[] coins2 = {2};
        int amount2 = 3;
        System.out.println("Test case 2: coins = " + Arrays.toString(coins2) + ", amount = " + amount2);
        System.out.println("Bottom-up DP: " + solution.coinChangeBottomUp(coins2, amount2)); // -1
        System.out.println("Top-down DP: " + solution.coinChangeTopDown(coins2, amount2)); // -1
        System.out.println("BFS: " + solution.coinChangeBFS(coins2, amount2)); // -1
        System.out.println();
        
        // Test case 3: Amount is 0
        int[] coins3 = {1};
        int amount3 = 0;
        System.out.println("Test case 3: coins = " + Arrays.toString(coins3) + ", amount = " + amount3);
        System.out.println("Bottom-up DP: " + solution.coinChangeBottomUp(coins3, amount3)); // 0
        System.out.println("Top-down DP: " + solution.coinChangeTopDown(coins3, amount3)); // 0
        System.out.println();
        
        // Test case 4: Large amount
        int[] coins4 = {1, 2, 5};
        int amount4 = 11;
        System.out.println("Test case 4: coins = " + Arrays.toString(coins4) + ", amount = " + amount4);
        System.out.println("Bottom-up DP: " + solution.coinChangeBottomUp(coins4, amount4)); // 3 (5+5+1)
        System.out.println("Coin combination: " + solution.getCoinCombination(coins4, amount4));
        System.out.println("Number of ways: " + solution.coinChangeWays(coins4, amount4));
    }
} 