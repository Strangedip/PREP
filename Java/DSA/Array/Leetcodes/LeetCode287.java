// Given an array of integers nums containing n + 1 integers where each integer is in the range [1, n] inclusive.
// There is only one repeated number in nums, return this repeated number.
// You must solve the problem without modifying the array nums and uses only constant extra space.

import java.util.Arrays;

public class LeetCode287 {
    public static void main(String[] args) {
        int[] a = { 1, 3, 4, 2, 2 };

        System.out.println(Arrays.toString(a));
        System.out.println(findDuplicate(a));
    }

    static public int findDuplicate(int[] nums) {
        cyclicSort(a);
        for (int i = 0; i < nums.length; i++) {
            if (i + 1 != nums[i]) {
                return nums[i];
            }
        }
        return nums[nums.length];
    }

    static void cyclicSort(int[] a) {
        int i = 0;
        while (i < a.length) {
            if (a[i] != a[a[i] - 1] && a[i] - 1 < a.length) {
                int temp = a[a[i] - 1];
                a[a[i] - 1] = a[i];
                a[i] = temp;
            } else {
                i++;
            }
        }
    }
}
