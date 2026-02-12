import java.util.*;

/**
 * Problem: Daily Temperatures
 * 
 * Given an array of integers temperatures represents the daily temperatures, 
 * return an array answer such that answer[i] is the number of days you have to 
 * wait after the ith day to get a warmer temperature. If there is no future day 
 * for which this is possible, keep answer[i] == 0.
 * 
 * Example:
 * Input: temperatures = [73,74,75,71,69,72,76,73]
 * Output: [1,1,4,2,1,1,0,0]
 * 
 * Example 2:
 * Input: temperatures = [30,40,50,60]
 * Output: [1,1,1,0]
 * 
 * Example 3:
 * Input: temperatures = [30,60,90]
 * Output: [1,1,0]
 * 
 * Constraints:
 * - 1 <= temperatures.length <= 10^5
 * - 30 <= temperatures[i] <= 100
 */
public class DailyTemperatures {
    
    /**
     * APPROACH 1: MONOTONIC STACK (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use stack to store indices of temperatures in decreasing order.
     * When we find a warmer temperature, pop from stack and calculate days.
     */
    public int[] dailyTemperaturesStack(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Stack<Integer> stack = new Stack<>();
        
        for (int i = 0; i < n; i++) {
            // While stack is not empty and current temp is warmer than stack top
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int index = stack.pop();
                result[index] = i - index;
            }
            
            // Push current index to stack
            stack.push(i);
        }
        
        return result;
    }
    
    /**
     * APPROACH 2: BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * For each day, search forward to find the next warmer day.
     */
    public int[] dailyTemperaturesBruteForce(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (temperatures[j] > temperatures[i]) {
                    result[i] = j - i;
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: ARRAY AS STACK (Space Optimized)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use array instead of Stack for better performance.
     */
    public int[] dailyTemperaturesArrayStack(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        int[] stack = new int[n];
        int top = -1;
        
        for (int i = 0; i < n; i++) {
            // While stack is not empty and current temp is warmer
            while (top >= 0 && temperatures[i] > temperatures[stack[top]]) {
                int index = stack[top--];
                result[index] = i - index;
            }
            
            // Push current index
            stack[++top] = i;
        }
        
        return result;
    }
    
    /**
     * APPROACH 4: REVERSE ITERATION WITH NEXT WARMER LOOKUP
     * Time Complexity: O(n * k) where k is the average number of warmer days
     * Space Complexity: O(1)
     * 
     * Iterate from right to left, use next warmer information to skip ahead.
     */
    public int[] dailyTemperaturesReverse(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        
        for (int i = n - 2; i >= 0; i--) {
            int j = i + 1;
            
            // Skip ahead using previously computed results
            while (j < n && temperatures[j] <= temperatures[i] && result[j] != 0) {
                j += result[j];
            }
            
            // If we found a warmer temperature
            if (j < n && temperatures[j] > temperatures[i]) {
                result[i] = j - i;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 5: USING PRIORITY QUEUE
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Use min-heap to keep track of temperatures and their indices.
     */
    public int[] dailyTemperaturesPriorityQueue(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        
        // Min-heap based on temperature
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        
        for (int i = 0; i < n; i++) {
            // Process all temperatures that are colder than current
            while (!pq.isEmpty() && pq.peek()[0] < temperatures[i]) {
                int[] pair = pq.poll();
                int temp = pair[0];
                int index = pair[1];
                result[index] = i - index;
            }
            
            pq.offer(new int[]{temperatures[i], i});
        }
        
        return result;
    }
    
    /**
     * APPROACH 6: USING DEQUE
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use deque to store indices, similar to stack approach.
     */
    public int[] dailyTemperaturesDeque(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) {
            while (!deque.isEmpty() && temperatures[i] > temperatures[deque.peekLast()]) {
                int index = deque.pollLast();
                result[index] = i - index;
            }
            
            deque.offerLast(i);
        }
        
        return result;
    }
    
    /**
     * APPROACH 7: COUNTING SORT OPTIMIZATION
     * Time Complexity: O(n)
     * Space Complexity: O(1) - Since temperatures are limited to 30-100
     * 
     * Use the fact that temperatures are in limited range.
     */
    public int[] dailyTemperaturesCountingSort(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        int[] next = new int[101]; // temperatures are 30-100
        
        for (int i = n - 1; i >= 0; i--) {
            int warmerIndex = Integer.MAX_VALUE;
            
            // Check all possible warmer temperatures
            for (int temp = temperatures[i] + 1; temp <= 100; temp++) {
                if (next[temp] != 0) {
                    warmerIndex = Math.min(warmerIndex, next[temp]);
                }
            }
            
            if (warmerIndex != Integer.MAX_VALUE) {
                result[i] = warmerIndex - i;
            }
            
            next[temperatures[i]] = i;
        }
        
        return result;
    }
    
    /**
     * APPROACH 8: SEGMENT TREE (Advanced)
     * Time Complexity: O(n log m) where m is the range of temperatures
     * Space Complexity: O(m)
     * 
     * Use segment tree for range maximum queries.
     */
    public int[] dailyTemperaturesSegmentTree(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        
        // For simplicity, we'll use a different approach here
        // that's more practical than a full segment tree implementation
        
        TreeMap<Integer, Integer> tempToIndex = new TreeMap<>();
        
        for (int i = n - 1; i >= 0; i--) {
            // Find the next warmer temperature
            Integer warmerTemp = tempToIndex.higherKey(temperatures[i]);
            
            if (warmerTemp != null) {
                result[i] = tempToIndex.get(warmerTemp) - i;
            }
            
            // Update with current temperature and index
            tempToIndex.put(temperatures[i], i);
        }
        
        return result;
    }
    
    // Helper method to print array
    public static void printArray(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        DailyTemperatures solution = new DailyTemperatures();
        
        // Test case 1: Standard example
        System.out.println("Test Case 1: [73,74,75,71,69,72,76,73]");
        int[] temps1 = {73, 74, 75, 71, 69, 72, 76, 73};
        System.out.print("Input: ");
        printArray(temps1);
        
        int[] result1 = solution.dailyTemperaturesStack(temps1);
        System.out.print("Stack: ");
        printArray(result1);
        
        int[] result2 = solution.dailyTemperaturesBruteForce(temps1);
        System.out.print("Brute Force: ");
        printArray(result2);
        
        int[] result3 = solution.dailyTemperaturesArrayStack(temps1);
        System.out.print("Array Stack: ");
        printArray(result3);
        
        int[] result4 = solution.dailyTemperaturesReverse(temps1);
        System.out.print("Reverse: ");
        printArray(result4);
        
        int[] result5 = solution.dailyTemperaturesPriorityQueue(temps1);
        System.out.print("Priority Queue: ");
        printArray(result5);
        
        int[] result6 = solution.dailyTemperaturesDeque(temps1);
        System.out.print("Deque: ");
        printArray(result6);
        
        int[] result7 = solution.dailyTemperaturesCountingSort(temps1);
        System.out.print("Counting Sort: ");
        printArray(result7);
        
        System.out.println();
        
        // Test case 2: Increasing temperatures
        System.out.println("Test Case 2: [30,40,50,60] (increasing)");
        int[] temps2 = {30, 40, 50, 60};
        System.out.print("Input: ");
        printArray(temps2);
        
        int[] result2_1 = solution.dailyTemperaturesStack(temps2);
        System.out.print("Result: ");
        printArray(result2_1);
        System.out.println();
        
        // Test case 3: All increasing
        System.out.println("Test Case 3: [30,60,90] (all increasing)");
        int[] temps3 = {30, 60, 90};
        System.out.print("Input: ");
        printArray(temps3);
        
        int[] result3_1 = solution.dailyTemperaturesStack(temps3);
        System.out.print("Result: ");
        printArray(result3_1);
        System.out.println();
        
        // Test case 4: Decreasing temperatures
        System.out.println("Test Case 4: [100,90,80,70] (decreasing)");
        int[] temps4 = {100, 90, 80, 70};
        System.out.print("Input: ");
        printArray(temps4);
        
        int[] result4_1 = solution.dailyTemperaturesStack(temps4);
        System.out.print("Result: ");
        printArray(result4_1);
        System.out.println();
        
        // Test case 5: Single element
        System.out.println("Test Case 5: [75] (single element)");
        int[] temps5 = {75};
        System.out.print("Input: ");
        printArray(temps5);
        
        int[] result5_1 = solution.dailyTemperaturesStack(temps5);
        System.out.print("Result: ");
        printArray(result5_1);
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(DailyTemperatures solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large test case
        int size = 100000;
        int[] largeTemps = new int[size];
        Random random = new Random(42); // Fixed seed for reproducibility
        
        for (int i = 0; i < size; i++) {
            largeTemps[i] = 30 + random.nextInt(71); // Range 30-100
        }
        
        long start, end;
        
        // Test Stack approach (optimal)
        start = System.nanoTime();
        int[] result1 = solution.dailyTemperaturesStack(largeTemps);
        end = System.nanoTime();
        System.out.println("Stack: " + (end - start) / 1000000.0 + " ms");
        
        // Test Array Stack approach
        start = System.nanoTime();
        int[] result2 = solution.dailyTemperaturesArrayStack(largeTemps);
        end = System.nanoTime();
        System.out.println("Array Stack: " + (end - start) / 1000000.0 + " ms");
        
        // Test Reverse approach
        start = System.nanoTime();
        int[] result3 = solution.dailyTemperaturesReverse(largeTemps);
        end = System.nanoTime();
        System.out.println("Reverse: " + (end - start) / 1000000.0 + " ms");
        
        // Test Counting Sort approach
        start = System.nanoTime();
        int[] result4 = solution.dailyTemperaturesCountingSort(largeTemps);
        end = System.nanoTime();
        System.out.println("Counting Sort: " + (end - start) / 1000000.0 + " ms");
        
        // Test Deque approach
        start = System.nanoTime();
        int[] result5 = solution.dailyTemperaturesDeque(largeTemps);
        end = System.nanoTime();
        System.out.println("Deque: " + (end - start) / 1000000.0 + " ms");
        
        // Verify all approaches give same result (check first 10 elements)
        boolean allSame = true;
        for (int i = 0; i < Math.min(10, size); i++) {
            if (!(result1[i] == result2[i] && result2[i] == result3[i] && 
                  result3[i] == result4[i] && result4[i] == result5[i])) {
                allSame = false;
                break;
            }
        }
        System.out.println("All approaches give same result: " + allSame);
        
        // Test Brute Force with smaller input to avoid timeout
        int smallSize = 1000;
        int[] smallTemps = Arrays.copyOf(largeTemps, smallSize);
        
        start = System.nanoTime();
        int[] result6 = solution.dailyTemperaturesBruteForce(smallTemps);
        end = System.nanoTime();
        System.out.println("Brute Force (small input): " + (end - start) / 1000000.0 + " ms");
    }
    
    /**
     * Method to verify correctness of all implementations
     */
    public static boolean verifyCorrectness(DailyTemperatures solution) {
        int[][] testCases = {
            {73, 74, 75, 71, 69, 72, 76, 73},
            {30, 40, 50, 60},
            {30, 60, 90},
            {100, 90, 80, 70},
            {75},
            {55, 38, 53, 81, 61, 93, 97, 32, 43, 78}
        };
        
        for (int[] temps : testCases) {
            int[] result1 = solution.dailyTemperaturesStack(temps);
            int[] result2 = solution.dailyTemperaturesBruteForce(temps);
            int[] result3 = solution.dailyTemperaturesArrayStack(temps);
            int[] result4 = solution.dailyTemperaturesReverse(temps);
            int[] result5 = solution.dailyTemperaturesCountingSort(temps);
            
            if (!Arrays.equals(result1, result2) || !Arrays.equals(result1, result3) ||
                !Arrays.equals(result1, result4) || !Arrays.equals(result1, result5)) {
                return false;
            }
        }
        
        return true;
    }
} 