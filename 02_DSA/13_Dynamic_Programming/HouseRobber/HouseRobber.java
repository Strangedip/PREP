import java.util.*;

/**
 * LeetCode 198: House Robber (and variations)
 * 
 * You are a professional robber planning to rob houses along a street.
 * Each house has a certain amount of money stashed, the only constraint stopping you from
 * robbing each of them is that adjacent houses have security systems connected and
 * it will automatically contact the police if two adjacent houses were broken into on the same night.
 * 
 * Given an integer array nums representing the amount of money of each house,
 * return the maximum amount of money you can rob tonight without alerting the police.
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1) optimized
 */
public class HouseRobber {
    
    /**
     * House Robber I: Linear arrangement
     * Approach 1: Bottom-Up DP with O(n) space
     */
    public int robLinear(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        
        for (int i = 2; i < nums.length; i++) {
            // Either rob current house + best from i-2, or skip current house
            dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]);
        }
        
        return dp[nums.length - 1];
    }
    
    /**
     * House Robber I: Optimized with O(1) space
     */
    public int robLinearOptimized(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        
        int prev2 = nums[0];  // dp[i-2]
        int prev1 = Math.max(nums[0], nums[1]);  // dp[i-1]
        
        for (int i = 2; i < nums.length; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }
    
    /**
     * House Robber I: Top-Down DP (Memoization)
     */
    public int robLinearTopDown(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        
        int[] memo = new int[nums.length];
        Arrays.fill(memo, -1);
        return robHelper(nums, nums.length - 1, memo);
    }
    
    private int robHelper(int[] nums, int i, int[] memo) {
        if (i < 0) return 0;
        if (i == 0) return nums[0];
        
        if (memo[i] != -1) return memo[i];
        
        // Either rob current house or skip it
        memo[i] = Math.max(robHelper(nums, i-1, memo), 
                          robHelper(nums, i-2, memo) + nums[i]);
        return memo[i];
    }
    
    /**
     * House Robber II: Circular arrangement (LeetCode 213)
     * Houses arranged in a circle - first and last house are adjacent
     */
    public int robCircular(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        if (nums.length == 2) return Math.max(nums[0], nums[1]);
        
        // Case 1: Rob houses 0 to n-2 (exclude last house)
        int maxExcludingLast = robRange(nums, 0, nums.length - 2);
        
        // Case 2: Rob houses 1 to n-1 (exclude first house)
        int maxExcludingFirst = robRange(nums, 1, nums.length - 1);
        
        return Math.max(maxExcludingLast, maxExcludingFirst);
    }
    
    private int robRange(int[] nums, int start, int end) {
        int prev2 = 0, prev1 = 0;
        
        for (int i = start; i <= end; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }
    
    /**
     * House Robber III: Binary Tree arrangement (LeetCode 337)
     * Houses arranged as binary tree - cannot rob direct parent/child
     */
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    
    public int robTree(TreeNode root) {
        int[] result = robTreeHelper(root);
        return Math.max(result[0], result[1]);
    }
    
    /**
     * Returns array: [maxWithoutRobbingRoot, maxWithRobbingRoot]
     */
    private int[] robTreeHelper(TreeNode root) {
        if (root == null) return new int[]{0, 0};
        
        int[] left = robTreeHelper(root.left);
        int[] right = robTreeHelper(root.right);
        
        // If we rob current node, we cannot rob its children
        int withRoot = root.val + left[0] + right[0];
        
        // If we don't rob current node, we can choose optimally from children
        int withoutRoot = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        
        return new int[]{withoutRoot, withRoot};
    }
    
    /**
     * House Robber III: Alternative implementation with memoization
     */
    public int robTreeMemo(TreeNode root) {
        Map<TreeNode, Integer> memo = new HashMap<>();
        return robTreeMemoHelper(root, memo);
    }
    
    private int robTreeMemoHelper(TreeNode root, Map<TreeNode, Integer> memo) {
        if (root == null) return 0;
        if (memo.containsKey(root)) return memo.get(root);
        
        // Option 1: Rob current node + grandchildren
        int robCurrent = root.val;
        if (root.left != null) {
            robCurrent += robTreeMemoHelper(root.left.left, memo) + 
                         robTreeMemoHelper(root.left.right, memo);
        }
        if (root.right != null) {
            robCurrent += robTreeMemoHelper(root.right.left, memo) + 
                         robTreeMemoHelper(root.right.right, memo);
        }
        
        // Option 2: Skip current node, rob children
        int skipCurrent = robTreeMemoHelper(root.left, memo) + 
                         robTreeMemoHelper(root.right, memo);
        
        int result = Math.max(robCurrent, skipCurrent);
        memo.put(root, result);
        return result;
    }
    
    /**
     * Extension: House Robber with K constraint
     * Cannot rob any K consecutive houses
     */
    public int robWithKConstraint(int[] nums, int k) {
        if (nums == null || nums.length == 0) return 0;
        if (k <= 0) return 0;
        if (k == 1) return Arrays.stream(nums).max().orElse(0);
        
        int n = nums.length;
        // dp[i] = maximum money can rob from houses 0 to i
        int[] dp = new int[n];
        
        // Fill base cases
        for (int i = 0; i < Math.min(k, n); i++) {
            dp[i] = nums[i];
            if (i > 0) dp[i] = Math.max(dp[i], dp[i-1]);
        }
        
        for (int i = k; i < n; i++) {
            dp[i] = Math.max(dp[i-1], dp[i-k] + nums[i]);
        }
        
        return dp[n-1];
    }
    
    /**
     * Helper method to create a test binary tree
     */
    public static TreeNode createTestTree() {
        /*
         * Create tree:     3
         *                /   \
         *               2     3
         *                \     \
         *                 3     1
         */
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.right = new TreeNode(3);
        root.right.right = new TreeNode(1);
        return root;
    }
    
    public static void main(String[] args) {
        HouseRobber solution = new HouseRobber();
        
        // Test House Robber I (Linear)
        int[] houses1 = {1, 2, 3, 1};
        System.out.println("House Robber I (Linear):");
        System.out.println("Houses: " + Arrays.toString(houses1));
        System.out.println("Bottom-up DP: " + solution.robLinear(houses1)); // 4
        System.out.println("Optimized: " + solution.robLinearOptimized(houses1)); // 4
        System.out.println("Top-down DP: " + solution.robLinearTopDown(houses1)); // 4
        System.out.println();
        
        // Test House Robber II (Circular)
        int[] houses2 = {2, 3, 2};
        System.out.println("House Robber II (Circular):");
        System.out.println("Houses: " + Arrays.toString(houses2));
        System.out.println("Circular: " + solution.robCircular(houses2)); // 3
        System.out.println();
        
        int[] houses3 = {1, 2, 3, 1};
        System.out.println("Houses: " + Arrays.toString(houses3));
        System.out.println("Circular: " + solution.robCircular(houses3)); // 4
        System.out.println();
        
        // Test House Robber III (Binary Tree)
        TreeNode root = createTestTree();
        System.out.println("House Robber III (Binary Tree):");
        System.out.println("Tree structure: 3 -> (2, 3) -> (null, 3), (null, 1)");
        System.out.println("Optimized: " + solution.robTree(root)); // 7
        System.out.println("Memoization: " + solution.robTreeMemo(root)); // 7
        System.out.println();
        
        // Test House Robber with K constraint
        int[] houses4 = {2, 7, 9, 3, 1};
        int k = 3;
        System.out.println("House Robber with K=" + k + " constraint:");
        System.out.println("Houses: " + Arrays.toString(houses4));
        System.out.println("Result: " + solution.robWithKConstraint(houses4, k)); // 10
        
        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " + solution.robLinear(new int[]{})); // 0
        System.out.println("Single house: " + solution.robLinear(new int[]{5})); // 5
        System.out.println("Two houses: " + solution.robLinear(new int[]{1, 2})); // 2
    }
} 