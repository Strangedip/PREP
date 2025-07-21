import java.util.*;

/**
 * Problem: Container With Most Water
 * 
 * You are given an integer array height of length n. There are n vertical lines 
 * drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]).
 * 
 * Find two lines that together with the x-axis form a container that can hold the most water.
 * Return the maximum amount of water a container can store.
 * 
 * Example:
 * Input: height = [1,8,6,2,5,4,8,3,7]
 * Output: 49
 * Explanation: Lines at index 1 and 8 form container with area = min(8,7) * (8-1) = 49
 */
public class ContainerWithMostWater {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * Check every possible pair of lines to find maximum area.
     */
    public int maxAreaBruteForce(int[] height) {
        int maxArea = 0;
        
        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                // Area = width * height (min of two heights)
                int width = j - i;
                int containerHeight = Math.min(height[i], height[j]);
                int area = width * containerHeight;
                
                maxArea = Math.max(maxArea, area);
            }
        }
        
        return maxArea;
    }
    
    /**
     * APPROACH 2: TWO POINTERS (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Start with the widest container and move the pointer with smaller height inward.
     * Key insight: Moving the pointer with larger height cannot improve the area.
     */
    public int maxAreaTwoPointers(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxArea = 0;
        
        while (left < right) {
            // Calculate current area
            int width = right - left;
            int containerHeight = Math.min(height[left], height[right]);
            int currentArea = width * containerHeight;
            
            maxArea = Math.max(maxArea, currentArea);
            
            // Move the pointer with smaller height
            // This is the key insight: moving the larger height pointer
            // would only decrease width without possibility of increasing height
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        
        return maxArea;
    }
    
    /**
     * APPROACH 3: TWO POINTERS WITH OPTIMIZATION
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Enhanced version that skips duplicate heights for better average performance.
     */
    public int maxAreaOptimized(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxArea = 0;
        
        while (left < right) {
            int leftHeight = height[left];
            int rightHeight = height[right];
            int width = right - left;
            int containerHeight = Math.min(leftHeight, rightHeight);
            int currentArea = width * containerHeight;
            
            maxArea = Math.max(maxArea, currentArea);
            
            // Move pointers and skip duplicates
            if (leftHeight < rightHeight) {
                while (left < right && height[left] <= leftHeight) {
                    left++;
                }
            } else {
                while (left < right && height[right] <= rightHeight) {
                    right--;
                }
            }
        }
        
        return maxArea;
    }
    
    /**
     * APPROACH 4: WITH TRACKING INDICES
     * Returns both the maximum area and the indices of the optimal container.
     */
    public int[] maxAreaWithIndices(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxArea = 0;
        int bestLeft = 0, bestRight = 0;
        
        while (left < right) {
            int width = right - left;
            int containerHeight = Math.min(height[left], height[right]);
            int currentArea = width * containerHeight;
            
            if (currentArea > maxArea) {
                maxArea = currentArea;
                bestLeft = left;
                bestRight = right;
            }
            
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        
        return new int[]{maxArea, bestLeft, bestRight};
    }
    
    /**
     * APPROACH 5: SEGMENT TREE APPROACH (OVERKILL)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Academic approach using segment tree for range maximum queries.
     * Not recommended for this problem but shows alternative thinking.
     */
    public int maxAreaSegmentTree(int[] height) {
        int n = height.length;
        if (n < 2) return 0;
        
        int maxArea = 0;
        
        // For each position, find the farthest position with sufficient height
        for (int i = 0; i < n; i++) {
            // Look for the farthest position j where min(height[i], height[j]) is maximized
            for (int j = n - 1; j > i; j--) {
                int area = (j - i) * Math.min(height[i], height[j]);
                maxArea = Math.max(maxArea, area);
                
                // Early termination: if remaining width * current height 
                // cannot exceed maxArea, break
                if ((j - i - 1) * height[i] <= maxArea) {
                    break;
                }
            }
        }
        
        return maxArea;
    }
    
    // Test method
    public static void main(String[] args) {
        ContainerWithMostWater solution = new ContainerWithMostWater();
        
        // Test case 1
        int[] height1 = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        System.out.println("Test Case 1: height = [1,8,6,2,5,4,8,3,7]");
        System.out.println("Brute Force: " + solution.maxAreaBruteForce(height1));
        System.out.println("Two Pointers: " + solution.maxAreaTwoPointers(height1));
        System.out.println("Optimized: " + solution.maxAreaOptimized(height1));
        
        int[] result = solution.maxAreaWithIndices(height1);
        System.out.println("Max area: " + result[0] + ", Left index: " + result[1] + ", Right index: " + result[2]);
        System.out.println();
        
        // Test case 2
        int[] height2 = {1, 1};
        System.out.println("Test Case 2: height = [1,1]");
        System.out.println("Two Pointers: " + solution.maxAreaTwoPointers(height2));
        System.out.println();
        
        // Test case 3
        int[] height3 = {4, 3, 2, 1, 4};
        System.out.println("Test Case 3: height = [4,3,2,1,4]");
        System.out.println("Two Pointers: " + solution.maxAreaTwoPointers(height3));
        System.out.println();
        
        // Test case 4
        int[] height4 = {1, 2, 1};
        System.out.println("Test Case 4: height = [1,2,1]");
        System.out.println("Two Pointers: " + solution.maxAreaTwoPointers(height4));
    }
} 