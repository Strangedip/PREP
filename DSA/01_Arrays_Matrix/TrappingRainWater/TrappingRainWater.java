import java.util.*;

/**
 * Problem: Trapping Rain Water
 * 
 * Given n non-negative integers representing an elevation map where the width 
 * of each bar is 1, compute how much water it can trap after raining.
 * 
 * Example:
 * Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
 * Output: 6
 * Explanation: The elevation map (black section) is represented by array [0,1,0,2,1,0,1,3,2,1,2,1]. 
 * In this case, 6 units of rain water (blue section) are being trapped.
 */
public class TrappingRainWater {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * For each position, find the maximum height to the left and right,
     * then calculate water that can be trapped at that position.
     */
    public int trapBruteForce(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        int n = height.length;
        int totalWater = 0;
        
        // For each position (except first and last)
        for (int i = 1; i < n - 1; i++) {
            // Find maximum height to the left
            int leftMax = 0;
            for (int j = 0; j < i; j++) {
                leftMax = Math.max(leftMax, height[j]);
            }
            
            // Find maximum height to the right
            int rightMax = 0;
            for (int j = i + 1; j < n; j++) {
                rightMax = Math.max(rightMax, height[j]);
            }
            
            // Water level at position i is min of left and right max
            int waterLevel = Math.min(leftMax, rightMax);
            
            // Water trapped = water level - ground level (if positive)
            if (waterLevel > height[i]) {
                totalWater += waterLevel - height[i];
            }
        }
        
        return totalWater;
    }
    
    /**
     * APPROACH 2: DYNAMIC PROGRAMMING
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Precompute the maximum heights to the left and right for each position.
     */
    public int trapDP(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        int n = height.length;
        
        // Precompute left max for each position
        int[] leftMax = new int[n];
        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }
        
        // Precompute right max for each position
        int[] rightMax = new int[n];
        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }
        
        // Calculate trapped water
        int totalWater = 0;
        for (int i = 0; i < n; i++) {
            int waterLevel = Math.min(leftMax[i], rightMax[i]);
            totalWater += waterLevel - height[i];
        }
        
        return totalWater;
    }
    
    /**
     * APPROACH 3: TWO POINTERS (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use two pointers moving from both ends, keeping track of max heights.
     * This is the most elegant and efficient solution.
     */
    public int trapTwoPointers(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int totalWater = 0;
        
        while (left < right) {
            if (height[left] < height[right]) {
                // Process left side
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    totalWater += leftMax - height[left];
                }
                left++;
            } else {
                // Process right side
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    totalWater += rightMax - height[right];
                }
                right--;
            }
        }
        
        return totalWater;
    }
    
    /**
     * APPROACH 4: USING STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use a stack to keep track of bars and calculate water when we find
     * a taller bar that can form a container.
     */
    public int trapStack(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        Stack<Integer> stack = new Stack<>();
        int totalWater = 0;
        
        for (int i = 0; i < height.length; i++) {
            // While stack is not empty and current height is greater than 
            // height at stack top, we can trap water
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = stack.pop();
                
                // If stack is empty, no left boundary
                if (stack.isEmpty()) break;
                
                int left = stack.peek();
                int width = i - left - 1;
                int waterHeight = Math.min(height[i], height[left]) - height[bottom];
                totalWater += width * waterHeight;
            }
            
            stack.push(i);
        }
        
        return totalWater;
    }
    
    /**
     * APPROACH 5: DIVIDE AND CONQUER (Advanced)
     * Time Complexity: O(n log n) on average, O(n²) worst case
     * Space Complexity: O(log n) for recursion stack
     * 
     * Find the maximum height and divide the problem into left and right parts.
     */
    public int trapDivideConquer(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        return trapHelper(height, 0, height.length - 1);
    }
    
    private int trapHelper(int[] height, int left, int right) {
        if (left >= right) return 0;
        
        // Find the maximum height in the range
        int maxIndex = left;
        for (int i = left + 1; i <= right; i++) {
            if (height[i] > height[maxIndex]) {
                maxIndex = i;
            }
        }
        
        // Calculate water trapped to the left of max
        int leftWater = 0;
        int leftMaxHeight = 0;
        for (int i = left; i < maxIndex; i++) {
            if (height[i] > leftMaxHeight) {
                leftMaxHeight = height[i];
            } else {
                leftWater += leftMaxHeight - height[i];
            }
        }
        
        // Calculate water trapped to the right of max
        int rightWater = 0;
        int rightMaxHeight = 0;
        for (int i = right; i > maxIndex; i--) {
            if (height[i] > rightMaxHeight) {
                rightMaxHeight = height[i];
            } else {
                rightWater += rightMaxHeight - height[i];
            }
        }
        
        return leftWater + rightWater + 
               trapHelper(height, left, maxIndex - 1) + 
               trapHelper(height, maxIndex + 1, right);
    }
    
    // Helper method to visualize the elevation map
    private void visualizeElevation(int[] height) {
        if (height == null || height.length == 0) return;
        
        int maxHeight = Arrays.stream(height).max().orElse(0);
        
        // Print from top to bottom
        for (int level = maxHeight; level > 0; level--) {
            for (int i = 0; i < height.length; i++) {
                if (height[i] >= level) {
                    System.out.print("█ ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        
        // Print indices
        for (int i = 0; i < height.length; i++) {
            System.out.print(i % 10 + " ");
        }
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        TrappingRainWater solution = new TrappingRainWater();
        
        // Test case 1: Classic example
        int[] height1 = {0,1,0,2,1,0,1,3,2,1,2,1};
        System.out.println("Test Case 1: [0,1,0,2,1,0,1,3,2,1,2,1]");
        solution.visualizeElevation(height1);
        System.out.println("Brute Force: " + solution.trapBruteForce(height1.clone()));
        System.out.println("DP: " + solution.trapDP(height1.clone()));
        System.out.println("Two Pointers: " + solution.trapTwoPointers(height1.clone()));
        System.out.println("Stack: " + solution.trapStack(height1.clone()));
        System.out.println("Divide & Conquer: " + solution.trapDivideConquer(height1.clone()));
        System.out.println();
        
        // Test case 2: Simple valley
        int[] height2 = {3,0,2,0,4};
        System.out.println("Test Case 2: [3,0,2,0,4]");
        solution.visualizeElevation(height2);
        System.out.println("Two Pointers: " + solution.trapTwoPointers(height2));
        System.out.println();
        
        // Test case 3: No water can be trapped
        int[] height3 = {1,2,3,4,5};
        System.out.println("Test Case 3: [1,2,3,4,5] (increasing)");
        System.out.println("Two Pointers: " + solution.trapTwoPointers(height3));
        System.out.println();
        
        // Test case 4: Decreasing heights
        int[] height4 = {5,4,3,2,1};
        System.out.println("Test Case 4: [5,4,3,2,1] (decreasing)");
        System.out.println("Two Pointers: " + solution.trapTwoPointers(height4));
        System.out.println();
        
        // Test case 5: All same height
        int[] height5 = {3,3,3,3};
        System.out.println("Test Case 5: [3,3,3,3] (all same)");
        System.out.println("Two Pointers: " + solution.trapTwoPointers(height5));
        System.out.println();
        
        // Test case 6: Single peak
        int[] height6 = {0,2,0};
        System.out.println("Test Case 6: [0,2,0] (single peak)");
        System.out.println("Two Pointers: " + solution.trapTwoPointers(height6));
    }
} 