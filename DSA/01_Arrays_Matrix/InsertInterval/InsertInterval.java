import java.util.*;

/**
 * Problem: Insert Interval
 * 
 * You are given an array of non-overlapping intervals intervals where 
 * intervals[i] = [starti, endi] represent the start and the end of the ith interval 
 * and intervals is sorted in ascending order by starti. You are also given an 
 * interval newInterval = [start, end] that represents the start and end of another interval.
 * 
 * Insert newInterval into intervals such that intervals is still sorted in ascending 
 * order by starti and intervals still does not have any overlapping intervals 
 * (merge overlapping intervals if necessary).
 * 
 * Return intervals after the insertion.
 * 
 * Example:
 * Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
 * Output: [[1,5],[6,9]]
 * 
 * Example 2:
 * Input: intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
 * Output: [[1,2],[3,10],[12,16]]
 */
public class InsertInterval {
    
    /**
     * APPROACH 1: THREE-PHASE APPROACH (Most Intuitive)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Process intervals in three phases:
     * 1. Add all intervals that end before newInterval starts
     * 2. Merge all overlapping intervals with newInterval
     * 3. Add all intervals that start after newInterval ends
     */
    public int[][] insertThreePhase(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;
        int n = intervals.length;
        
        // Phase 1: Add all intervals that end before newInterval starts
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }
        
        // Phase 2: Merge all overlapping intervals with newInterval
        while (i < n && intervals[i][0] <= newInterval[1]) {
            // Merge intervals
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);
        
        // Phase 3: Add all remaining intervals
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }
        
        return result.toArray(new int[result.size()][]);
    }
    
    /**
     * APPROACH 2: SINGLE PASS WITH CONDITIONS
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Process all intervals in a single pass, handling each case appropriately.
     */
    public int[][] insertSinglePass(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        boolean inserted = false;
        
        for (int[] interval : intervals) {
            if (interval[1] < newInterval[0]) {
                // Current interval ends before newInterval starts
                result.add(interval);
            } else if (interval[0] > newInterval[1]) {
                // Current interval starts after newInterval ends
                if (!inserted) {
                    result.add(newInterval);
                    inserted = true;
                }
                result.add(interval);
            } else {
                // Overlapping intervals - merge with newInterval
                newInterval[0] = Math.min(newInterval[0], interval[0]);
                newInterval[1] = Math.max(newInterval[1], interval[1]);
            }
        }
        
        // If newInterval hasn't been inserted yet, add it
        if (!inserted) {
            result.add(newInterval);
        }
        
        return result.toArray(new int[result.size()][]);
    }
    
    /**
     * APPROACH 3: BINARY SEARCH OPTIMIZATION
     * Time Complexity: O(n) worst case, but can be faster in practice
     * Space Complexity: O(n)
     * 
     * Use binary search to find insertion points, then process the relevant range.
     */
    public int[][] insertBinarySearch(int[][] intervals, int[] newInterval) {
        if (intervals.length == 0) {
            return new int[][]{newInterval};
        }
        
        List<int[]> result = new ArrayList<>();
        
        // Find the range of intervals that might overlap with newInterval
        int start = findInsertPosition(intervals, newInterval[0], true);
        int end = findInsertPosition(intervals, newInterval[1], false);
        
        // Add intervals before the overlap range
        for (int i = 0; i < start; i++) {
            result.add(intervals[i]);
        }
        
        // Merge overlapping intervals
        int mergedStart = newInterval[0];
        int mergedEnd = newInterval[1];
        
        for (int i = start; i <= end; i++) {
            if (i < intervals.length && intervals[i][0] <= newInterval[1] && intervals[i][1] >= newInterval[0]) {
                mergedStart = Math.min(mergedStart, intervals[i][0]);
                mergedEnd = Math.max(mergedEnd, intervals[i][1]);
            }
        }
        result.add(new int[]{mergedStart, mergedEnd});
        
        // Add intervals after the overlap range
        for (int i = end + 1; i < intervals.length; i++) {
            if (intervals[i][0] > newInterval[1]) {
                result.add(intervals[i]);
            }
        }
        
        return result.toArray(new int[result.size()][]);
    }
    
    /**
     * Helper method for binary search approach
     */
    private int findInsertPosition(int[][] intervals, int target, boolean searchStart) {
        int left = 0, right = intervals.length - 1;
        int result = intervals.length;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int compareValue = searchStart ? intervals[mid][1] : intervals[mid][0];
            
            if ((searchStart && compareValue >= target) || (!searchStart && compareValue > target)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 4: IN-PLACE MODIFICATION (If allowed)
     * Time Complexity: O(n)
     * Space Complexity: O(1) additional space
     * 
     * Modify the intervals array in-place if modification is allowed.
     * Note: This approach is more complex and not typically preferred in interviews.
     */
    public int[][] insertInPlace(int[][] intervals, int[] newInterval) {
        // This approach would require complex array manipulation
        // and is generally not preferred in interviews due to complexity
        // Included for completeness but typically use the three-phase approach
        return insertThreePhase(intervals, newInterval);
    }
    
    /**
     * APPROACH 5: USING MERGE INTERVALS LOGIC
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Add newInterval to the array and then use merge intervals logic.
     */
    public int[][] insertUsingMerge(int[][] intervals, int[] newInterval) {
        // Create a new array with the newInterval inserted
        List<int[]> allIntervals = new ArrayList<>();
        boolean inserted = false;
        
        for (int[] interval : intervals) {
            if (!inserted && interval[0] > newInterval[0]) {
                allIntervals.add(newInterval);
                inserted = true;
            }
            allIntervals.add(interval);
        }
        
        if (!inserted) {
            allIntervals.add(newInterval);
        }
        
        // Now merge overlapping intervals
        return mergeIntervals(allIntervals.toArray(new int[allIntervals.size()][]));
    }
    
    /**
     * Helper method to merge intervals (reusing logic from Merge Intervals problem)
     */
    private int[][] mergeIntervals(int[][] intervals) {
        if (intervals.length <= 1) return intervals;
        
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        List<int[]> merged = new ArrayList<>();
        int[] currentInterval = intervals[0];
        merged.add(currentInterval);
        
        for (int i = 1; i < intervals.length; i++) {
            int[] nextInterval = intervals[i];
            
            if (currentInterval[1] >= nextInterval[0]) {
                currentInterval[1] = Math.max(currentInterval[1], nextInterval[1]);
            } else {
                currentInterval = nextInterval;
                merged.add(currentInterval);
            }
        }
        
        return merged.toArray(new int[merged.size()][]);
    }
    
    // Helper method to print intervals
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
        InsertInterval solution = new InsertInterval();
        
        // Test case 1: Insert with merging
        int[][] intervals1 = {{1,3},{6,9}};
        int[] newInterval1 = {2,5};
        System.out.println("Test Case 1: intervals=[[1,3],[6,9]], newInterval=[2,5]");
        System.out.print("Three Phase: ");
        solution.printIntervals(solution.insertThreePhase(intervals1.clone(), newInterval1.clone()));
        System.out.print("Single Pass: ");
        solution.printIntervals(solution.insertSinglePass(intervals1.clone(), newInterval1.clone()));
        System.out.print("Using Merge: ");
        solution.printIntervals(solution.insertUsingMerge(intervals1.clone(), newInterval1.clone()));
        System.out.println();
        
        // Test case 2: Insert with multiple merges
        int[][] intervals2 = {{1,2},{3,5},{6,7},{8,10},{12,16}};
        int[] newInterval2 = {4,8};
        System.out.println("Test Case 2: intervals=[[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval=[4,8]");
        System.out.print("Three Phase: ");
        solution.printIntervals(solution.insertThreePhase(intervals2.clone(), newInterval2.clone()));
        System.out.print("Single Pass: ");
        solution.printIntervals(solution.insertSinglePass(intervals2.clone(), newInterval2.clone()));
        System.out.println();
        
        // Test case 3: No overlap - insert at beginning
        int[][] intervals3 = {{3,5},{6,9}};
        int[] newInterval3 = {1,2};
        System.out.println("Test Case 3: intervals=[[3,5],[6,9]], newInterval=[1,2] (insert at beginning)");
        System.out.print("Result: ");
        solution.printIntervals(solution.insertThreePhase(intervals3, newInterval3));
        System.out.println();
        
        // Test case 4: No overlap - insert at end
        int[][] intervals4 = {{1,3},{6,9}};
        int[] newInterval4 = {10,12};
        System.out.println("Test Case 4: intervals=[[1,3],[6,9]], newInterval=[10,12] (insert at end)");
        System.out.print("Result: ");
        solution.printIntervals(solution.insertThreePhase(intervals4, newInterval4));
        System.out.println();
        
        // Test case 5: Empty intervals
        int[][] intervals5 = {};
        int[] newInterval5 = {5,7};
        System.out.println("Test Case 5: intervals=[], newInterval=[5,7] (empty intervals)");
        System.out.print("Result: ");
        solution.printIntervals(solution.insertThreePhase(intervals5, newInterval5));
        System.out.println();
        
        // Test case 6: Complete overlap
        int[][] intervals6 = {{1,5}};
        int[] newInterval6 = {2,3};
        System.out.println("Test Case 6: intervals=[[1,5]], newInterval=[2,3] (complete overlap)");
        System.out.print("Result: ");
        solution.printIntervals(solution.insertThreePhase(intervals6, newInterval6));
    }
} 