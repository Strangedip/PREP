import java.util.*;

/**
 * Problem: Next Greater Element I
 * 
 * The next greater element of some element x in an array is the first greater element 
 * that is to the right of x in the same array.
 * 
 * You are given two distinct 0-indexed integer arrays nums1 and nums2, where nums1 is 
 * a subset of nums2.
 * 
 * For each 0 <= i < nums1.length, find the index j such that nums1[i] == nums2[j] and 
 * determine the next greater element of nums2[j] in nums2. If there is no next greater 
 * element, then the answer for this query is -1.
 * 
 * Return an array ans of length nums1.length such that ans[i] is the next greater 
 * element as described above.
 * 
 * Example:
 * Input: nums1 = [4,1,2], nums2 = [1,3,4,2,5]
 * Output: [5,3,-1]
 * Explanation: The next greater element for each value of nums1 is as follows:
 * - 4 is underlined in nums2 = [1,3,4,2,5]. There is no next greater element, so the answer is -1.
 * - 1 is underlined in nums2 = [1,3,4,2,5]. The next greater element is 3.
 * - 2 is underlined in nums2 = [1,3,4,2,5]. There is no next greater element, so the answer is -1.
 * 
 * Example 2:
 * Input: nums1 = [2,4], nums2 = [1,2,3,4]
 * Output: [3,-1]
 * 
 * Constraints:
 * - 1 <= nums1.length <= nums2.length <= 1000
 * - 0 <= nums1[i], nums2[i] <= 10^4
 * - All integers in nums1 and nums2 are unique.
 * - All the integers of nums1 also appear in nums2.
 */
public class NextGreaterElement {
    
    /**
     * APPROACH 1: MONOTONIC STACK + HASHMAP (Optimal)
     * Time Complexity: O(m + n) where m = nums1.length, n = nums2.length
     * Space Complexity: O(n)
     * 
     * Use stack to find next greater elements, then HashMap for quick lookup.
     */
    public int[] nextGreaterElementOptimal(int[] nums1, int[] nums2) {
        // Map to store next greater element for each number in nums2
        Map<Integer, Integer> nextGreaterMap = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        
        // Process nums2 to find next greater elements
        for (int num : nums2) {
            // While stack is not empty and current number is greater than stack top
            while (!stack.isEmpty() && num > stack.peek()) {
                nextGreaterMap.put(stack.pop(), num);
            }
            stack.push(num);
        }
        
        // For remaining elements in stack, there's no next greater element
        while (!stack.isEmpty()) {
            nextGreaterMap.put(stack.pop(), -1);
        }
        
        // Build result array for nums1
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreaterMap.get(nums1[i]);
        }
        
        return result;
    }
    
    /**
     * APPROACH 2: BRUTE FORCE
     * Time Complexity: O(m * n)
     * Space Complexity: O(1) excluding result array
     * 
     * For each element in nums1, find it in nums2 and search for next greater.
     */
    public int[] nextGreaterElementBruteForce(int[] nums1, int[] nums2) {
        int[] result = new int[nums1.length];
        
        for (int i = 0; i < nums1.length; i++) {
            int target = nums1[i];
            boolean found = false;
            result[i] = -1;
            
            // Find target in nums2
            for (int j = 0; j < nums2.length; j++) {
                if (nums2[j] == target) {
                    found = true;
                } else if (found && nums2[j] > target) {
                    result[i] = nums2[j];
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: HASHMAP + LINEAR SEARCH
     * Time Complexity: O(m * n)
     * Space Complexity: O(n)
     * 
     * Use HashMap to store indices, then search linearly for next greater.
     */
    public int[] nextGreaterElementHashMap(int[] nums1, int[] nums2) {
        // Map value to index in nums2
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < nums2.length; i++) {
            indexMap.put(nums2[i], i);
        }
        
        int[] result = new int[nums1.length];
        
        for (int i = 0; i < nums1.length; i++) {
            int target = nums1[i];
            int startIndex = indexMap.get(target);
            result[i] = -1;
            
            // Search for next greater element
            for (int j = startIndex + 1; j < nums2.length; j++) {
                if (nums2[j] > target) {
                    result[i] = nums2[j];
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 4: USING DEQUE
     * Time Complexity: O(m + n)
     * Space Complexity: O(n)
     * 
     * Use Deque instead of Stack for next greater element calculation.
     */
    public int[] nextGreaterElementDeque(int[] nums1, int[] nums2) {
        Map<Integer, Integer> nextGreaterMap = new HashMap<>();
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (int num : nums2) {
            while (!deque.isEmpty() && num > deque.peekLast()) {
                nextGreaterMap.put(deque.pollLast(), num);
            }
            deque.offerLast(num);
        }
        
        // Elements remaining in deque have no next greater element
        while (!deque.isEmpty()) {
            nextGreaterMap.put(deque.pollLast(), -1);
        }
        
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreaterMap.get(nums1[i]);
        }
        
        return result;
    }
    
    /**
     * APPROACH 5: REVERSE PROCESSING
     * Time Complexity: O(m + n)
     * Space Complexity: O(n)
     * 
     * Process nums2 from right to left using stack.
     */
    public int[] nextGreaterElementReverse(int[] nums1, int[] nums2) {
        Map<Integer, Integer> nextGreaterMap = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        
        // Process nums2 from right to left
        for (int i = nums2.length - 1; i >= 0; i--) {
            int num = nums2[i];
            
            // Pop smaller or equal elements
            while (!stack.isEmpty() && stack.peek() <= num) {
                stack.pop();
            }
            
            // Next greater element is top of stack (or -1 if empty)
            nextGreaterMap.put(num, stack.isEmpty() ? -1 : stack.peek());
            
            // Push current element
            stack.push(num);
        }
        
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreaterMap.get(nums1[i]);
        }
        
        return result;
    }
    
    /**
     * APPROACH 6: TREEMAP APPROACH
     * Time Complexity: O(m + n * log n)
     * Space Complexity: O(n)
     * 
     * Use TreeMap to find next greater element efficiently.
     */
    public int[] nextGreaterElementTreeMap(int[] nums1, int[] nums2) {
        Map<Integer, Integer> nextGreaterMap = new HashMap<>();
        
        for (int i = 0; i < nums2.length; i++) {
            int current = nums2[i];
            nextGreaterMap.put(current, -1);
            
            // Find next greater element
            for (int j = i + 1; j < nums2.length; j++) {
                if (nums2[j] > current) {
                    nextGreaterMap.put(current, nums2[j]);
                    break;
                }
            }
        }
        
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreaterMap.get(nums1[i]);
        }
        
        return result;
    }
    
    /**
     * APPROACH 7: USING ARRAYLIST AS STACK
     * Time Complexity: O(m + n)
     * Space Complexity: O(n)
     * 
     * Use ArrayList to simulate stack operations.
     */
    public int[] nextGreaterElementArrayList(int[] nums1, int[] nums2) {
        Map<Integer, Integer> nextGreaterMap = new HashMap<>();
        List<Integer> stack = new ArrayList<>();
        
        for (int num : nums2) {
            while (!stack.isEmpty() && num > stack.get(stack.size() - 1)) {
                nextGreaterMap.put(stack.remove(stack.size() - 1), num);
            }
            stack.add(num);
        }
        
        // Remaining elements have no next greater element
        for (int num : stack) {
            nextGreaterMap.put(num, -1);
        }
        
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreaterMap.get(nums1[i]);
        }
        
        return result;
    }
    
    /**
     * BONUS: Next Greater Element II (Circular Array)
     * For reference - handles circular array case
     */
    public int[] nextGreaterElementCircular(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Stack<Integer> stack = new Stack<>();
        
        // Process array twice to handle circular nature
        for (int i = 0; i < 2 * n; i++) {
            while (!stack.isEmpty() && nums[i % n] > nums[stack.peek()]) {
                result[stack.pop()] = nums[i % n];
            }
            
            if (i < n) {
                stack.push(i);
            }
        }
        
        return result;
    }
    
    /**
     * BONUS: Next Greater Element with Indices
     * Returns indices instead of values
     */
    public int[] nextGreaterElementIndices(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Stack<Integer> stack = new Stack<>();
        
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
                result[stack.pop()] = i;
            }
            stack.push(i);
        }
        
        return result;
    }
    
    // Helper method to print array
    public static void printArray(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        NextGreaterElement solution = new NextGreaterElement();
        
        // Test case 1: Standard example
        System.out.println("Test Case 1: nums1=[4,1,2], nums2=[1,3,4,2,5]");
        int[] nums1_1 = {4, 1, 2};
        int[] nums2_1 = {1, 3, 4, 2, 5};
        
        System.out.print("nums1: ");
        printArray(nums1_1);
        System.out.print("nums2: ");
        printArray(nums2_1);
        
        int[] result1 = solution.nextGreaterElementOptimal(nums1_1, nums2_1);
        System.out.print("Optimal: ");
        printArray(result1);
        
        int[] result2 = solution.nextGreaterElementBruteForce(nums1_1, nums2_1);
        System.out.print("Brute Force: ");
        printArray(result2);
        
        int[] result3 = solution.nextGreaterElementHashMap(nums1_1, nums2_1);
        System.out.print("HashMap: ");
        printArray(result3);
        
        int[] result4 = solution.nextGreaterElementDeque(nums1_1, nums2_1);
        System.out.print("Deque: ");
        printArray(result4);
        
        int[] result5 = solution.nextGreaterElementReverse(nums1_1, nums2_1);
        System.out.print("Reverse: ");
        printArray(result5);
        
        int[] result6 = solution.nextGreaterElementArrayList(nums1_1, nums2_1);
        System.out.print("ArrayList: ");
        printArray(result6);
        
        System.out.println();
        
        // Test case 2: Second example
        System.out.println("Test Case 2: nums1=[2,4], nums2=[1,2,3,4]");
        int[] nums1_2 = {2, 4};
        int[] nums2_2 = {1, 2, 3, 4};
        
        System.out.print("nums1: ");
        printArray(nums1_2);
        System.out.print("nums2: ");
        printArray(nums2_2);
        
        int[] result2_1 = solution.nextGreaterElementOptimal(nums1_2, nums2_2);
        System.out.print("Result: ");
        printArray(result2_1);
        System.out.println();
        
        // Test case 3: No greater elements
        System.out.println("Test Case 3: nums1=[3,2,1], nums2=[3,2,1] (decreasing)");
        int[] nums1_3 = {3, 2, 1};
        int[] nums2_3 = {3, 2, 1};
        
        int[] result3_1 = solution.nextGreaterElementOptimal(nums1_3, nums2_3);
        System.out.print("Result: ");
        printArray(result3_1);
        System.out.println();
        
        // Test case 4: All have greater elements
        System.out.println("Test Case 4: nums1=[1,2,3], nums2=[1,2,3,4] (increasing)");
        int[] nums1_4 = {1, 2, 3};
        int[] nums2_4 = {1, 2, 3, 4};
        
        int[] result4_1 = solution.nextGreaterElementOptimal(nums1_4, nums2_4);
        System.out.print("Result: ");
        printArray(result4_1);
        System.out.println();
        
        // Test case 5: Single elements
        System.out.println("Test Case 5: nums1=[5], nums2=[5] (single element)");
        int[] nums1_5 = {5};
        int[] nums2_5 = {5};
        
        int[] result5_1 = solution.nextGreaterElementOptimal(nums1_5, nums2_5);
        System.out.print("Result: ");
        printArray(result5_1);
        System.out.println();
        
        // Bonus test cases
        System.out.println("Bonus: Next Greater Element II (Circular)");
        int[] circular = {1, 2, 1};
        System.out.print("Circular [1,2,1]: ");
        printArray(solution.nextGreaterElementCircular(circular));
        
        System.out.println("Bonus: Next Greater Element Indices");
        int[] forIndices = {2, 1, 2, 4, 3, 1};
        System.out.print("Indices [2,1,2,4,3,1]: ");
        printArray(solution.nextGreaterElementIndices(forIndices));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(NextGreaterElement solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large test case
        int size1 = 1000;
        int size2 = 1000;
        
        int[] nums1 = new int[size1];
        int[] nums2 = new int[size2];
        
        // nums1 is subset of nums2
        for (int i = 0; i < size2; i++) {
            nums2[i] = i + 1;
        }
        
        for (int i = 0; i < size1; i++) {
            nums1[i] = i + 1;
        }
        
        long start, end;
        
        // Test Optimal approach
        start = System.nanoTime();
        int[] result1 = solution.nextGreaterElementOptimal(nums1, nums2);
        end = System.nanoTime();
        System.out.println("Optimal: " + (end - start) / 1000000.0 + " ms");
        
        // Test HashMap approach
        start = System.nanoTime();
        int[] result2 = solution.nextGreaterElementHashMap(nums1, nums2);
        end = System.nanoTime();
        System.out.println("HashMap: " + (end - start) / 1000000.0 + " ms");
        
        // Test Deque approach
        start = System.nanoTime();
        int[] result3 = solution.nextGreaterElementDeque(nums1, nums2);
        end = System.nanoTime();
        System.out.println("Deque: " + (end - start) / 1000000.0 + " ms");
        
        // Test Reverse approach
        start = System.nanoTime();
        int[] result4 = solution.nextGreaterElementReverse(nums1, nums2);
        end = System.nanoTime();
        System.out.println("Reverse: " + (end - start) / 1000000.0 + " ms");
        
        // Test ArrayList approach
        start = System.nanoTime();
        int[] result5 = solution.nextGreaterElementArrayList(nums1, nums2);
        end = System.nanoTime();
        System.out.println("ArrayList: " + (end - start) / 1000000.0 + " ms");
        
        // Test Brute Force with smaller input
        int smallSize = 100;
        int[] smallNums1 = Arrays.copyOf(nums1, smallSize);
        int[] smallNums2 = Arrays.copyOf(nums2, smallSize);
        
        start = System.nanoTime();
        int[] result6 = solution.nextGreaterElementBruteForce(smallNums1, smallNums2);
        end = System.nanoTime();
        System.out.println("Brute Force (small input): " + (end - start) / 1000000.0 + " ms");
        
        // Verify all approaches give same result (check first 10 elements)
        boolean allSame = true;
        for (int i = 0; i < Math.min(10, size1); i++) {
            if (!(result1[i] == result2[i] && result2[i] == result3[i] && 
                  result3[i] == result4[i] && result4[i] == result5[i])) {
                allSame = false;
                break;
            }
        }
        System.out.println("All approaches give same result: " + allSame);
    }
    
    /**
     * Method to verify correctness with various test cases
     */
    public static boolean verifyCorrectness(NextGreaterElement solution) {
        int[][][] testCases = {
            {{4, 1, 2}, {1, 3, 4, 2, 5}},    // [5, 3, -1]
            {{2, 4}, {1, 2, 3, 4}},          // [3, -1]
            {{1, 3, 5, 2, 4}, {6, 5, 4, 3, 2, 1, 7}}, // [7, 7, 7, 7, 7]
            {{5}, {5}},                       // [-1]
            {{1, 2, 3}, {1, 2, 3, 4, 5}}     // [2, 3, 4]
        };
        
        int[][] expected = {
            {5, 3, -1},
            {3, -1},
            {7, 7, 7, 7, 7},
            {-1},
            {2, 3, 4}
        };
        
        for (int i = 0; i < testCases.length; i++) {
            int[] result1 = solution.nextGreaterElementOptimal(testCases[i][0], testCases[i][1]);
            int[] result2 = solution.nextGreaterElementBruteForce(testCases[i][0], testCases[i][1]);
            int[] result3 = solution.nextGreaterElementHashMap(testCases[i][0], testCases[i][1]);
            
            if (!Arrays.equals(result1, expected[i]) || 
                !Arrays.equals(result2, expected[i]) || 
                !Arrays.equals(result3, expected[i])) {
                System.out.println("Test case " + i + " failed.");
                System.out.println("Expected: " + Arrays.toString(expected[i]));
                System.out.println("Got: " + Arrays.toString(result1));
                return false;
            }
        }
        
        return true;
    }
} 