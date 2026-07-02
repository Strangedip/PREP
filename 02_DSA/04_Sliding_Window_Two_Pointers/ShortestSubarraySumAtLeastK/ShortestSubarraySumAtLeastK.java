import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 862: Shortest Subarray with Sum at Least K
 *
 * Return the length of the shortest non-empty subarray whose sum is at least k.
 * Array may contain negative integers — standard sliding window does not apply.
 *
 * Time Complexity:
 *   - shortestSubarray:       O(n)
 *   - shortestSubarrayBrute:  O(n^2)
 * Space Complexity: O(n)
 */
public class ShortestSubarraySumAtLeastK {

    /**
     * Approach 1: Prefix Sum + Monotonic Deque (Optimal)
     *
     * Prefix[j] - Prefix[i] >= k gives subarray sum. Deque stores indices with
     * increasing prefix values so we can find the shortest valid window in O(n).
     */
    public int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        Deque<Integer> deque = new ArrayDeque<>();
        int answer = n + 1;

        for (int j = 0; j <= n; j++) {
            // Shrink from front: earliest start index that yields sum >= k
            while (!deque.isEmpty() && prefix[j] - prefix[deque.peekFirst()] >= k) {
                answer = Math.min(answer, j - deque.pollFirst());
            }
            // Maintain increasing prefix values — larger earlier prefix is never better
            while (!deque.isEmpty() && prefix[deque.peekLast()] >= prefix[j]) {
                deque.pollLast();
            }
            deque.offerLast(j);
        }

        return answer <= n ? answer : -1;
    }

    /**
     * Approach 2: Brute Force with Prefix Sums (Baseline)
     *
     * For each start index, scan forward until cumulative sum reaches k.
     * Correct but O(n^2) — useful to verify logic before optimizing.
     */
    public int shortestSubarrayBrute(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        int answer = n + 1;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (prefix[j] - prefix[i] >= k) {
                    answer = Math.min(answer, j - i);
                    break; // smallest j for this i found
                }
            }
        }
        return answer <= n ? answer : -1;
    }

    public static void main(String[] args) {
        ShortestSubarraySumAtLeastK solution = new ShortestSubarraySumAtLeastK();

        // Test case 1: single element
        int[] nums1 = {1};
        int k1 = 1;
        System.out.println("Test 1: nums=" + java.util.Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("  Deque:  " + solution.shortestSubarray(nums1, k1));       // 1
        System.out.println("  Brute:  " + solution.shortestSubarrayBrute(nums1, k1));  // 1
        System.out.println();

        // Test case 2: impossible
        int[] nums2 = {1, 2};
        int k2 = 4;
        System.out.println("Test 2: nums=" + java.util.Arrays.toString(nums2) + ", k=" + k2);
        System.out.println("  Deque:  " + solution.shortestSubarray(nums2, k2));       // -1
        System.out.println("  Brute:  " + solution.shortestSubarrayBrute(nums2, k2));  // -1
        System.out.println();

        // Test case 3: negatives — sliding window would fail
        int[] nums3 = {2, -1, 2};
        int k3 = 3;
        System.out.println("Test 3: nums=" + java.util.Arrays.toString(nums3) + ", k=" + k3);
        System.out.println("  Deque:  " + solution.shortestSubarray(nums3, k3));       // 3
        System.out.println("  Brute:  " + solution.shortestSubarrayBrute(nums3, k3));  // 3
        System.out.println();

        // Test case 4: longer array with negatives
        int[] nums4 = {84, -37, 32, 40, 95};
        int k4 = 167;
        System.out.println("Test 4: nums=" + java.util.Arrays.toString(nums4) + ", k=" + k4);
        System.out.println("  Deque:  " + solution.shortestSubarray(nums4, k4));       // 3
        System.out.println("  Brute:  " + solution.shortestSubarrayBrute(nums4, k4));  // 3
    }
}
