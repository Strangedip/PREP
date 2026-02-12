import java.util.*;

/**
 * Problem: Merge Intervals
 * 
 * Given an array of intervals where intervals[i] = [starti, endi], 
 * merge all overlapping intervals, and return an array of the 
 * non-overlapping intervals that cover all the intervals in the input.
 * 
 * Example:
 * Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
 * Output: [[1,6],[8,10],[15,18]]
 * Explanation: Since intervals [1,3] and [2,6] overlaps, merge them into [1,6].
 * 
 * Example 2:
 * Input: intervals = [[1,4],[4,5]]
 * Output: [[1,5]]
 * Explanation: Intervals [1,4] and [4,5] are considered overlapping.
 */
public class MergeIntervals {
    
    /**
     * APPROACH 1: SORT THEN MERGE (Optimal)
     * Time Complexity: O(n log n)
     * Space Complexity: O(log n) for sorting, O(n) for result
     * 
     * Sort intervals by start time, then merge overlapping ones.
     * This is the most efficient and intuitive approach.
     */
    public int[][] mergeOptimal(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        List<int[]> merged = new ArrayList<>();
        int[] currentInterval = intervals[0];
        merged.add(currentInterval);
        
        for (int i = 1; i < intervals.length; i++) {
            int[] nextInterval = intervals[i];
            
            // Check if current and next intervals overlap
            if (currentInterval[1] >= nextInterval[0]) {
                // Merge intervals by extending the end time
                currentInterval[1] = Math.max(currentInterval[1], nextInterval[1]);
            } else {
                // No overlap, add next interval as new current
                currentInterval = nextInterval;
                merged.add(currentInterval);
            }
        }
        
        return merged.toArray(new int[merged.size()][]);
    }
    
    /**
     * APPROACH 2: SORT THEN MERGE (Cleaner Implementation)
     * Time Complexity: O(n log n)
     * Space Complexity: O(log n) for sorting, O(n) for result
     * 
     * Similar to approach 1 but with cleaner logic separation.
     */
    public int[][] mergeCleaner(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        List<int[]> merged = new ArrayList<>();
        
        for (int[] interval : intervals) {
            // If merged is empty or no overlap with last interval
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                // Overlap found, merge with last interval
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }
        
        return merged.toArray(new int[merged.size()][]);
    }
    
    /**
     * APPROACH 3: USING STACK
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Use a stack to keep track of intervals, merging when overlap is found.
     */
    public int[][] mergeUsingStack(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        Stack<int[]> stack = new Stack<>();
        stack.push(intervals[0]);
        
        for (int i = 1; i < intervals.length; i++) {
            int[] top = stack.peek();
            int[] current = intervals[i];
            
            if (top[1] >= current[0]) {
                // Overlap found, merge intervals
                top[1] = Math.max(top[1], current[1]);
            } else {
                // No overlap, push current interval
                stack.push(current);
            }
        }
        
        return stack.toArray(new int[stack.size()][]);
    }
    
    /**
     * APPROACH 4: COORDINATE COMPRESSION (Advanced)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Use events (start/end) and sweep line algorithm.
     * More complex but demonstrates advanced algorithmic thinking.
     */
    public int[][] mergeCoordinateCompression(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Create events: +1 for start, -1 for end+1 (to handle adjacent intervals)
        List<int[]> events = new ArrayList<>();
        for (int[] interval : intervals) {
            events.add(new int[]{interval[0], 1});    // Start event
            events.add(new int[]{interval[1] + 1, -1}); // End event (exclusive)
        }
        
        // Sort events by time, then by type (starts before ends)
        events.sort((a, b) -> {
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(b[1], a[1]); // Start (+1) before End (-1)
        });
        
        List<int[]> merged = new ArrayList<>();
        int count = 0;
        int start = -1;
        
        for (int[] event : events) {
            if (count == 0 && event[1] == 1) {
                // Starting a new interval
                start = event[0];
            }
            
            count += event[1];
            
            if (count == 0 && event[1] == -1) {
                // Ending an interval
                merged.add(new int[]{start, event[0] - 1});
            }
        }
        
        return merged.toArray(new int[merged.size()][]);
    }
    
    /**
     * APPROACH 5: BRUTE FORCE (For Educational Purposes)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Check every pair of intervals for overlap and merge.
     * Not efficient but helps understand the problem.
     */
    public int[][] mergeBruteForce(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        List<int[]> merged = new ArrayList<>();
        boolean[] used = new boolean[intervals.length];
        
        for (int i = 0; i < intervals.length; i++) {
            if (used[i]) continue;
            
            int[] current = new int[]{intervals[i][0], intervals[i][1]};
            used[i] = true;
            
            // Find all intervals that overlap with current
            boolean foundOverlap;
            do {
                foundOverlap = false;
                for (int j = 0; j < intervals.length; j++) {
                    if (used[j]) continue;
                    
                    // Check if intervals overlap
                    if (isOverlapping(current, intervals[j])) {
                        // Merge intervals
                        current[0] = Math.min(current[0], intervals[j][0]);
                        current[1] = Math.max(current[1], intervals[j][1]);
                        used[j] = true;
                        foundOverlap = true;
                    }
                }
            } while (foundOverlap);
            
            merged.add(current);
        }
        
        // Sort result by start time
        merged.sort((a, b) -> Integer.compare(a[0], b[0]));
        
        return merged.toArray(new int[merged.size()][]);
    }
    
    /**
     * Helper method to check if two intervals overlap
     */
    private boolean isOverlapping(int[] interval1, int[] interval2) {
        return interval1[1] >= interval2[0] && interval2[1] >= interval1[0];
    }
    
    // Helper method to print intervals in a readable format
    private void printIntervals(int[][] intervals) {
        System.out.print("[");
        for (int i = 0; i < intervals.length; i++) {
            System.out.print("[" + intervals[i][0] + "," + intervals[i][1] + "]");
            if (i < intervals.length - 1) System.out.print(",");
        }
        System.out.println("]");
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        MergeIntervals solution = new MergeIntervals();
        
        // Test case 1: Basic overlapping intervals
        int[][] intervals1 = {{1,3},{2,6},{8,10},{15,18}};
        System.out.println("Test Case 1: [[1,3],[2,6],[8,10],[15,18]]");
        System.out.print("Optimal: ");
        solution.printIntervals(solution.mergeOptimal(intervals1.clone()));
        System.out.print("Cleaner: ");
        solution.printIntervals(solution.mergeCleaner(intervals1.clone()));
        System.out.print("Stack: ");
        solution.printIntervals(solution.mergeUsingStack(intervals1.clone()));
        System.out.print("Coordinate: ");
        solution.printIntervals(solution.mergeCoordinateCompression(intervals1.clone()));
        System.out.print("Brute Force: ");
        solution.printIntervals(solution.mergeBruteForce(intervals1.clone()));
        System.out.println();
        
        // Test case 2: Adjacent intervals (touching)
        int[][] intervals2 = {{1,4},{4,5}};
        System.out.println("Test Case 2: [[1,4],[4,5]] (touching intervals)");
        System.out.print("Result: ");
        solution.printIntervals(solution.mergeOptimal(intervals2));
        System.out.println();
        
        // Test case 3: No overlaps
        int[][] intervals3 = {{1,2},{3,4},{5,6}};
        System.out.println("Test Case 3: [[1,2],[3,4],[5,6]] (no overlaps)");
        System.out.print("Result: ");
        solution.printIntervals(solution.mergeOptimal(intervals3));
        System.out.println();
        
        // Test case 4: All intervals overlap
        int[][] intervals4 = {{1,4},{2,5},{3,6}};
        System.out.println("Test Case 4: [[1,4],[2,5],[3,6]] (all overlap)");
        System.out.print("Result: ");
        solution.printIntervals(solution.mergeOptimal(intervals4));
        System.out.println();
        
        // Test case 5: Unsorted input
        int[][] intervals5 = {{15,18},{2,6},{8,10},{1,3}};
        System.out.println("Test Case 5: [[15,18],[2,6],[8,10],[1,3]] (unsorted)");
        System.out.print("Result: ");
        solution.printIntervals(solution.mergeOptimal(intervals5));
        System.out.println();
        
        // Test case 6: Single interval
        int[][] intervals6 = {{1,4}};
        System.out.println("Test Case 6: [[1,4]] (single interval)");
        System.out.print("Result: ");
        solution.printIntervals(solution.mergeOptimal(intervals6));
    }
} 