import java.util.Arrays;

/**
 * LeetCode 287: Find the Duplicate Number
 *
 * Array nums has n+1 integers in range [1, n] with exactly one duplicate.
 * Return the duplicate number.
 *
 * Time Complexity:
 *   - findDuplicateFloyd:      O(n)
 *   - findDuplicateCyclicSort: O(n)
 *   - findDuplicateBinarySearch: O(n log n)
 * Space Complexity: O(1) for all approaches
 */
public class FindDuplicateNumber {

    /**
     * Approach 1: Floyd's Tortoise and Hare (Optimal — no mutation)
     *
     * Model the array as a linked list: index i points to nums[i].
     * Duplicate value creates a cycle; cycle entry point equals the duplicate.
     */
    public int findDuplicateFloyd(int[] nums) {
        int slow = nums[0];
        int fast = nums[0];

        // Phase 1: detect cycle (must advance at least once)
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Phase 2: find cycle entry — the duplicate number
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }

    /**
     * Approach 2: Cyclic Sort (Optimal when mutation allowed)
     *
     * Place value v at index v-1. When nums[i] would swap to a position
     * that already holds the same value, nums[i] is the duplicate.
     */
    public int findDuplicateCyclicSort(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            int correct = nums[i] - 1;
            if (nums[i] != nums[correct]) {
                swap(nums, i, correct);
            } else {
                i++;
            }
        }
        // After sort, the misplaced slot holds the duplicate
        for (i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                return nums[i];
            }
        }
        return -1;
    }

    /**
     * Approach 3: Binary Search on Value (Fallback — O(n log n))
     *
     * Search [1, n]: count elements <= mid. If count > mid, duplicate is
     * in the lower half (pigeonhole on prefix counts).
     */
    public int findDuplicateBinarySearch(int[] nums) {
        int lo = 1;
        int hi = nums.length - 1;

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int count = 0;
            for (int x : nums) {
                if (x <= mid) {
                    count++;
                }
            }
            if (count > mid) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        FindDuplicateNumber solution = new FindDuplicateNumber();

        // Test case 1: standard example
        int[] nums1 = {1, 3, 4, 2, 2};
        System.out.println("Test 1: " + Arrays.toString(nums1));
        System.out.println("  Floyd:         " + solution.findDuplicateFloyd(nums1.clone()));
        System.out.println("  Cyclic sort:   " + solution.findDuplicateCyclicSort(nums1.clone()));
        System.out.println("  Binary search: " + solution.findDuplicateBinarySearch(nums1.clone()));
        System.out.println();

        // Test case 2: duplicate early in array
        int[] nums2 = {3, 1, 3, 4, 2};
        System.out.println("Test 2: " + Arrays.toString(nums2));
        System.out.println("  Floyd:         " + solution.findDuplicateFloyd(nums2.clone()));
        System.out.println("  Cyclic sort:   " + solution.findDuplicateCyclicSort(nums2.clone()));
        System.out.println("  Binary search: " + solution.findDuplicateBinarySearch(nums2.clone()));
        System.out.println();

        // Test case 3: all same value
        int[] nums3 = {2, 2, 2, 2, 2};
        System.out.println("Test 3: " + Arrays.toString(nums3));
        System.out.println("  Floyd:         " + solution.findDuplicateFloyd(nums3.clone()));
        System.out.println("  Cyclic sort:   " + solution.findDuplicateCyclicSort(nums3.clone()));
        System.out.println("  Binary search: " + solution.findDuplicateBinarySearch(nums3.clone()));
        System.out.println();

        // Test case 4: minimum size
        int[] nums4 = {1, 1};
        System.out.println("Test 4: " + Arrays.toString(nums4));
        System.out.println("  Floyd:         " + solution.findDuplicateFloyd(nums4.clone()));
        System.out.println("  Cyclic sort:   " + solution.findDuplicateCyclicSort(nums4.clone()));
        System.out.println("  Binary search: " + solution.findDuplicateBinarySearch(nums4.clone()));
    }
}
