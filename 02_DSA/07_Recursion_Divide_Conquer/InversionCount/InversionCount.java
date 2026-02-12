/**
 * Inversion Count using Modified Merge Sort.
 * 
 * An inversion is (i, j) where i < j but nums[i] > nums[j].
 * 
 * Time: O(n log n) — merge sort with counting during merge step.
 * Space: O(n) — temporary array for merging.
 */
public class InversionCount {

    public int countInversions(int[] nums) {
        int[] temp = new int[nums.length];
        return mergeSortCount(nums, temp, 0, nums.length - 1);
    }

    private int mergeSortCount(int[] nums, int[] temp, int left, int right) {
        int count = 0;

        if (left < right) {
            int mid = left + (right - left) / 2;
            count += mergeSortCount(nums, temp, left, mid);
            count += mergeSortCount(nums, temp, mid + 1, right);
            count += mergeCount(nums, temp, left, mid, right);
        }

        return count;
    }

    private int mergeCount(int[] nums, int[] temp, int left, int mid, int right) {
        int i = left, j = mid + 1, k = left;
        int count = 0;

        while (i <= mid && j <= right) {
            if (nums[i] <= nums[j]) {
                temp[k++] = nums[i++];
            } else {
                count += (mid - i + 1);
                temp[k++] = nums[j++];
            }
        }

        while (i <= mid) temp[k++] = nums[i++];
        while (j <= right) temp[k++] = nums[j++];

        for (int idx = left; idx <= right; idx++) {
            nums[idx] = temp[idx];
        }

        return count;
    }

    public static void main(String[] args) {
        InversionCount solution = new InversionCount();

        System.out.println(solution.countInversions(new int[]{2, 4, 1, 3, 5}));     // 3
        System.out.println(solution.countInversions(new int[]{5, 4, 3, 2, 1}));     // 10
        System.out.println(solution.countInversions(new int[]{1, 2, 3, 4, 5}));     // 0
    }
}

