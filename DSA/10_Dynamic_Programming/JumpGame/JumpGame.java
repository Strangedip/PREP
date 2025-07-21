import java.util.*;

/**
 * Jump Game Problems
 * 
 * Collection of jump game variations:
 * 1. Jump Game I: Can reach the last index?
 * 2. Jump Game II: Minimum number of jumps to reach the end
 * 3. Jump Game III: Can reach any index with value 0?
 * 4. Jump Game IV: Minimum jumps with teleportation
 * 
 * Core concept: Given an array where each element represents the maximum 
 * jump length from that position, solve various reachability problems.
 */
public class JumpGame {
    
    /**
     * JUMP GAME I: CAN REACH LAST INDEX?
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Determine if you can reach the last index starting from the first index.
     */
    public boolean canJump(int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        
        int maxReach = 0;
        
        for (int i = 0; i < nums.length; i++) {
            // If current position is beyond max reachable, can't proceed
            if (i > maxReach) {
                return false;
            }
            
            // Update max reachable position
            maxReach = Math.max(maxReach, i + nums[i]);
            
            // Early termination: if we can reach or pass the last index
            if (maxReach >= nums.length - 1) {
                return true;
            }
        }
        
        return maxReach >= nums.length - 1;
    }
    
    /**
     * JUMP GAME I: DYNAMIC PROGRAMMING APPROACH
     * Time Complexity: O(n²) in worst case
     * Space Complexity: O(n)
     * 
     * Alternative DP solution for educational purposes.
     */
    public boolean canJumpDP(int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        
        int n = nums.length;
        boolean[] dp = new boolean[n];
        dp[0] = true; // Can always reach starting position
        
        for (int i = 0; i < n; i++) {
            if (!dp[i]) continue; // Can't reach position i
            
            // Try all possible jumps from position i
            for (int jump = 1; jump <= nums[i] && i + jump < n; jump++) {
                dp[i + jump] = true;
                
                // Early termination
                if (i + jump == n - 1) {
                    return true;
                }
            }
        }
        
        return dp[n - 1];
    }
    
    /**
     * JUMP GAME II: MINIMUM JUMPS TO REACH END
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Find minimum number of jumps needed to reach the last index.
     * Uses greedy approach with BFS-like thinking.
     */
    public int jump(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return 0;
        }
        
        int jumps = 0;
        int currentEnd = 0;     // End of current jump range
        int farthest = 0;       // Farthest position reachable
        
        for (int i = 0; i < nums.length - 1; i++) {
            // Update farthest reachable position
            farthest = Math.max(farthest, i + nums[i]);
            
            // If we've reached the end of current jump range
            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
                
                // Early termination
                if (currentEnd >= nums.length - 1) {
                    break;
                }
            }
        }
        
        return jumps;
    }
    
    /**
     * JUMP GAME II: DYNAMIC PROGRAMMING APPROACH
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Alternative DP solution for minimum jumps.
     */
    public int jumpDP(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return 0;
        }
        
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        
        for (int i = 0; i < n; i++) {
            if (dp[i] == Integer.MAX_VALUE) continue;
            
            for (int jump = 1; jump <= nums[i] && i + jump < n; jump++) {
                dp[i + jump] = Math.min(dp[i + jump], dp[i] + 1);
            }
        }
        
        return dp[n - 1] == Integer.MAX_VALUE ? -1 : dp[n - 1];
    }
    
    /**
     * JUMP GAME III: REACH INDEX WITH VALUE 0
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Can you reach any index with value 0? You can jump left or right.
     */
    public boolean canReach(int[] arr, int start) {
        if (arr == null || start < 0 || start >= arr.length) {
            return false;
        }
        
        boolean[] visited = new boolean[arr.length];
        return dfsCanReach(arr, start, visited);
    }
    
    private boolean dfsCanReach(int[] arr, int index, boolean[] visited) {
        // Out of bounds or already visited
        if (index < 0 || index >= arr.length || visited[index]) {
            return false;
        }
        
        // Found target (value 0)
        if (arr[index] == 0) {
            return true;
        }
        
        visited[index] = true;
        
        // Try jumping left and right
        return dfsCanReach(arr, index + arr[index], visited) ||
               dfsCanReach(arr, index - arr[index], visited);
    }
    
    /**
     * JUMP GAME III: BFS APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Alternative BFS solution for Jump Game III.
     */
    public boolean canReachBFS(int[] arr, int start) {
        if (arr == null || start < 0 || start >= arr.length) {
            return false;
        }
        
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[arr.length];
        
        queue.offer(start);
        visited[start] = true;
        
        while (!queue.isEmpty()) {
            int index = queue.poll();
            
            if (arr[index] == 0) {
                return true;
            }
            
            // Jump right
            int right = index + arr[index];
            if (right < arr.length && !visited[right]) {
                visited[right] = true;
                queue.offer(right);
            }
            
            // Jump left
            int left = index - arr[index];
            if (left >= 0 && !visited[left]) {
                visited[left] = true;
                queue.offer(left);
            }
        }
        
        return false;
    }
    
    /**
     * JUMP GAME IV: MINIMUM JUMPS WITH TELEPORTATION
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Can jump to i+1, i-1, or any index j where arr[j] == arr[i].
     * Find minimum jumps to reach last index.
     */
    public int minJumps(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return 0;
        }
        
        int n = arr.length;
        if (n == 2) return 1;
        
        // Build graph: value -> list of indices with that value
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.computeIfAbsent(arr[i], k -> new ArrayList<>()).add(i);
        }
        
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[n];
        
        queue.offer(0);
        visited[0] = true;
        int steps = 0;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            
            for (int i = 0; i < size; i++) {
                int index = queue.poll();
                
                if (index == n - 1) {
                    return steps;
                }
                
                // Jump to adjacent indices
                for (int next : Arrays.asList(index - 1, index + 1)) {
                    if (next >= 0 && next < n && !visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
                
                // Jump to indices with same value (teleportation)
                if (graph.containsKey(arr[index])) {
                    for (int next : graph.get(arr[index])) {
                        if (!visited[next]) {
                            visited[next] = true;
                            queue.offer(next);
                        }
                    }
                    // Clear to avoid revisiting (optimization)
                    graph.remove(arr[index]);
                }
            }
            
            steps++;
        }
        
        return -1; // Should not reach here if last index is reachable
    }
    
    /**
     * JUMP GAME V: MAXIMUM REACHABLE INDICES
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * From each index, can jump to any index j where |i-j| <= arr[i] and arr[j] < arr[i].
     * Find maximum number of indices you can visit starting from any index.
     */
    public int maxJumps(int[] arr, int d) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        
        int n = arr.length;
        int[] memo = new int[n];
        int maxVisits = 0;
        
        for (int i = 0; i < n; i++) {
            maxVisits = Math.max(maxVisits, dfsMaxJumps(arr, d, i, memo));
        }
        
        return maxVisits;
    }
    
    private int dfsMaxJumps(int[] arr, int d, int index, int[] memo) {
        if (memo[index] != 0) {
            return memo[index];
        }
        
        int maxReach = 1; // At least can visit current index
        
        // Try jumping left
        for (int i = index - 1; i >= 0 && i >= index - d && arr[i] < arr[index]; i--) {
            maxReach = Math.max(maxReach, 1 + dfsMaxJumps(arr, d, i, memo));
        }
        
        // Try jumping right
        for (int i = index + 1; i < arr.length && i <= index + d && arr[i] < arr[index]; i++) {
            maxReach = Math.max(maxReach, 1 + dfsMaxJumps(arr, d, i, memo));
        }
        
        memo[index] = maxReach;
        return maxReach;
    }
    
    /**
     * JUMP GAME VI: MAXIMUM SCORE WITH K JUMPS
     * Time Complexity: O(n log k) with priority queue, O(n) with deque
     * Space Complexity: O(k)
     * 
     * Each step can jump 1 to k indices. Maximize the sum of values visited.
     */
    public int maxResult(int[] nums, int k) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int n = nums.length;
        Deque<int[]> deque = new ArrayDeque<>(); // [index, score]
        deque.offer(new int[]{0, nums[0]});
        
        for (int i = 1; i < n; i++) {
            // Remove elements outside the window
            while (!deque.isEmpty() && deque.peekFirst()[0] < i - k) {
                deque.pollFirst();
            }
            
            // Current max score = best score from window + current value
            int currentScore = deque.peekFirst()[1] + nums[i];
            
            // Maintain decreasing order in deque
            while (!deque.isEmpty() && deque.peekLast()[1] <= currentScore) {
                deque.pollLast();
            }
            
            deque.offer(new int[]{i, currentScore});
        }
        
        return deque.peekLast()[1];
    }
    
    /**
     * Utility method to print array
     */
    private void printArray(int[] arr, String label) {
        System.out.print(label + ": ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        JumpGame solution = new JumpGame();
        
        // Test Case 1: Jump Game I
        System.out.println("=== Test Case 1: Jump Game I ===");
        int[] nums1 = {2, 3, 1, 1, 4};
        solution.printArray(nums1, "Array");
        System.out.println("Can reach end (Greedy): " + solution.canJump(nums1));
        System.out.println("Can reach end (DP): " + solution.canJumpDP(nums1));
        
        int[] nums1b = {3, 2, 1, 0, 4};
        solution.printArray(nums1b, "Array");
        System.out.println("Can reach end: " + solution.canJump(nums1b));
        System.out.println();
        
        // Test Case 2: Jump Game II
        System.out.println("=== Test Case 2: Jump Game II ===");
        int[] nums2 = {2, 3, 1, 1, 4};
        solution.printArray(nums2, "Array");
        System.out.println("Min jumps (Greedy): " + solution.jump(nums2));
        System.out.println("Min jumps (DP): " + solution.jumpDP(nums2));
        
        int[] nums2b = {2, 3, 0, 1, 4};
        solution.printArray(nums2b, "Array");
        System.out.println("Min jumps: " + solution.jump(nums2b));
        System.out.println();
        
        // Test Case 3: Jump Game III
        System.out.println("=== Test Case 3: Jump Game III ===");
        int[] arr3 = {4, 2, 3, 0, 3, 1, 2};
        int start3 = 5;
        solution.printArray(arr3, "Array");
        System.out.println("Start index: " + start3);
        System.out.println("Can reach 0 (DFS): " + solution.canReach(arr3, start3));
        System.out.println("Can reach 0 (BFS): " + solution.canReachBFS(arr3, start3));
        System.out.println();
        
        // Test Case 4: Jump Game IV
        System.out.println("=== Test Case 4: Jump Game IV ===");
        int[] arr4 = {100, -23, -23, 404, 100, 23, 23, 23, 3, 404};
        solution.printArray(arr4, "Array");
        System.out.println("Min jumps with teleportation: " + solution.minJumps(arr4));
        System.out.println();
        
        // Test Case 5: Jump Game V
        System.out.println("=== Test Case 5: Jump Game V ===");
        int[] arr5 = {6, 4, 14, 6, 8, 13, 9, 7, 10, 6, 12};
        int d5 = 2;
        solution.printArray(arr5, "Array");
        System.out.println("Max jump distance: " + d5);
        System.out.println("Max reachable indices: " + solution.maxJumps(arr5, d5));
        System.out.println();
        
        // Test Case 6: Jump Game VI
        System.out.println("=== Test Case 6: Jump Game VI ===");
        int[] nums6 = {1, -1, -2, 4, -7, 3};
        int k6 = 2;
        solution.printArray(nums6, "Array");
        System.out.println("Max jumps per step: " + k6);
        System.out.println("Maximum score: " + solution.maxResult(nums6, k6));
        System.out.println();
        
        // Performance comparison
        performanceTest(solution);
    }
    
    private static void performanceTest(JumpGame solution) {
        System.out.println("=== Performance Test ===");
        
        int[] sizes = {1000, 5000, 10000};
        
        for (int size : sizes) {
            // Generate test array
            int[] nums = new int[size];
            Random rand = new Random(42);
            for (int i = 0; i < size - 1; i++) {
                nums[i] = rand.nextInt(Math.min(10, size - i)) + 1; // Ensure reachability
            }
            nums[size - 1] = 0; // Last element
            
            System.out.println("Array size: " + size);
            
            long startTime, endTime;
            
            // Jump Game I - Greedy
            startTime = System.nanoTime();
            boolean result1 = solution.canJump(nums);
            endTime = System.nanoTime();
            System.out.println("Can Jump (Greedy): " + result1 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Jump Game II - Greedy
            startTime = System.nanoTime();
            int result2 = solution.jump(nums);
            endTime = System.nanoTime();
            System.out.println("Min Jumps (Greedy): " + result2 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // For smaller arrays, test DP approaches
            if (size <= 1000) {
                startTime = System.nanoTime();
                boolean result3 = solution.canJumpDP(nums);
                endTime = System.nanoTime();
                System.out.println("Can Jump (DP): " + result3 + " (" + 
                                 (endTime - startTime) / 1_000_000.0 + " ms)");
                
                startTime = System.nanoTime();
                int result4 = solution.jumpDP(nums);
                endTime = System.nanoTime();
                System.out.println("Min Jumps (DP): " + result4 + " (" + 
                                 (endTime - startTime) / 1_000_000.0 + " ms)");
                
                System.out.println("Results match: " + (result1 == result3 && result2 == result4));
            }
            
            System.out.println();
        }
    }
} 