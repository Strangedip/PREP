/**
 * LeetCode 4: Median of Two Sorted Arrays
 *
 * Given two sorted arrays nums1 and nums2, return the median of the two sorted arrays combined.
 * Optimal solution must run in O(log(m + n)).
 *
 * Time Complexity:
 *   - findMedianPartition: O(log min(m, n))
 *   - findMedianMerge:     O(m + n)
 * Space Complexity: O(1) for both approaches
 */
public class MedianOfTwoSortedArrays {

    /**
     * Approach 1: Binary Search on Partition (Optimal)
     *
     * Binary search the cut position in the shorter array. A valid partition places
     * all left-half elements ≤ all right-half elements. The median is derived from
     * the four boundary values around the cut.
     */
    public double findMedianPartition(int[] nums1, int[] nums2) {
        // Ensure nums1 is the shorter array so j stays in bounds
        if (nums1.length > nums2.length) {
            return findMedianPartition(nums2, nums1);
        }

        int m = nums1.length;
        int n = nums2.length;
        int lo = 0;
        int hi = m;

        while (lo <= hi) {
            int i = (lo + hi) / 2;
            // Left partition must contain (m + n + 1) / 2 elements (ceil for odd totals)
            int j = (m + n + 1) / 2 - i;

            // Boundary values around the cut; sentinels handle empty halves
            int maxLeft1 = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
            int minRight1 = (i == m) ? Integer.MAX_VALUE : nums1[i];
            int maxLeft2 = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
            int minRight2 = (j == n) ? Integer.MAX_VALUE : nums2[j];

            if (maxLeft1 <= minRight2 && maxLeft2 <= minRight1) {
                // Valid partition found
                if ((m + n) % 2 == 1) {
                    return Math.max(maxLeft1, maxLeft2);
                }
                return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
            } else if (maxLeft1 > minRight2) {
                hi = i - 1; // Cut too far right in nums1
            } else {
                lo = i + 1; // Cut too far left in nums1
            }
        }

        throw new IllegalArgumentException("Input arrays are not sorted");
    }

    /**
     * Approach 2: Two-Pointer Merge (Baseline)
     *
     * Simulates merging both arrays with two pointers until we reach the middle.
     * Correct but O(m + n) — use when interviewer allows non-optimal first pass.
     */
    public double findMedianMerge(int[] nums1, int[] nums2) {
        int m = nums1.length;
        int n = nums2.length;
        int i = 0;
        int j = 0;
        int prev = 0;
        int cur = 0;

        // Walk until we've processed (m + n) / 2 + 1 elements
        for (int count = 0; count <= (m + n) / 2; count++) {
            prev = cur;
            if (i < m && (j >= n || nums1[i] <= nums2[j])) {
                cur = nums1[i++];
            } else {
                cur = nums2[j++];
            }
        }

        if ((m + n) % 2 == 1) {
            return cur;
        }
        return (prev + cur) / 2.0;
    }

    public static void main(String[] args) {
        MedianOfTwoSortedArrays solution = new MedianOfTwoSortedArrays();

        // Test case 1: odd total length
        int[] nums1a = {1, 3};
        int[] nums2a = {2};
        System.out.println("Test 1: nums1=" + java.util.Arrays.toString(nums1a)
                + ", nums2=" + java.util.Arrays.toString(nums2a));
        System.out.println("  Partition: " + solution.findMedianPartition(nums1a, nums2a)); // 2.0
        System.out.println("  Merge:     " + solution.findMedianMerge(nums1a, nums2a));     // 2.0
        System.out.println();

        // Test case 2: even total length
        int[] nums1b = {1, 2};
        int[] nums2b = {3, 4};
        System.out.println("Test 2: nums1=" + java.util.Arrays.toString(nums1b)
                + ", nums2=" + java.util.Arrays.toString(nums2b));
        System.out.println("  Partition: " + solution.findMedianPartition(nums1b, nums2b)); // 2.5
        System.out.println("  Merge:     " + solution.findMedianMerge(nums1b, nums2b));     // 2.5
        System.out.println();

        // Test case 3: one array empty
        int[] nums1c = {};
        int[] nums2c = {1};
        System.out.println("Test 3: nums1=" + java.util.Arrays.toString(nums1c)
                + ", nums2=" + java.util.Arrays.toString(nums2c));
        System.out.println("  Partition: " + solution.findMedianPartition(nums1c, nums2c)); // 1.0
        System.out.println("  Merge:     " + solution.findMedianMerge(nums1c, nums2c));     // 1.0
        System.out.println();

        // Test case 4: different lengths
        int[] nums1d = {1, 2, 3, 4, 5};
        int[] nums2d = {6, 7, 8};
        System.out.println("Test 4: nums1=" + java.util.Arrays.toString(nums1d)
                + ", nums2=" + java.util.Arrays.toString(nums2d));
        System.out.println("  Partition: " + solution.findMedianPartition(nums1d, nums2d)); // 4.5
        System.out.println("  Merge:     " + solution.findMedianMerge(nums1d, nums2d));     // 4.5
    }
}
