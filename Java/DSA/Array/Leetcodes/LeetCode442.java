// Given an integer array nums of length n where all the integers of nums are in the range [1, n] and each integer appears once or twice
//  return an array of all the integers that appears twice.
// You must write an algorithm that runs in O(n) time and uses only constant extra space.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode442 {

    public static void main(String[] args) {
        int[] a = { 1, 3, 4, 2, 2 };

        System.out.println(Arrays.toString(a));
        System.out.println(findDuplicates(a));
    }

    static public List<Integer> findDuplicates(int[] nums) {
        cyclicSort(nums);
        List<Integer> a = new ArrayList<>();
        for (int i = nums.length - 1; i >= 0; i--) {
            if (i + 1 != nums[i]) {
                a.add(nums[i]);
            }
        }
        return a;
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

// use cyclic sort -> a[i] ~ i
// since it is ranged and have to find missing. use cyclic sort to sort in O(n). then search duplicates using linear search and add in new array.