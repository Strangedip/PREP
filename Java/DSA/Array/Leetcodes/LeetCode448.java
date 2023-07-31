// Given an array nums of n integers where nums[i] is in the range [1, n]
// Return an array of all the integers in the range [1, n] that do not appear in nums.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode448 {

    public static void main(String[] args) {
        int[] a = { 5, 6, 1, 7, 4 };
        cyclicSort(a);
        System.out.println(Arrays.toString(a));
        // System.out.println(findDisappearedNumbers(a));
    }

    public static List<Integer> findDisappearedNumbers(int[] nums) {
        cyclicSort(nums);
        ArrayList<Integer> a=new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (i+1 != nums[i]) {
                a.add(i);
            }
        }
        return a;
    }

    static void cyclicSort(int[] a) {
        // start with 0th index
        int i = 0;
        // i should be less than size of array and remain same till no swap is required
        while (i < a.length) {
            // the element should be less than size of the array else it will throw error
            // if element-1 is not equal to element index swap element to its n-1 position
            if (a[i] < a.length && i != a[i]) {
                int temp = a[a[i]-1];
                a[a[i]-1] = a[i];
                a[i] = temp;
            } else {
                // if element is at right position increase i by one (no swap required)
                i++;
            }
        }
    }
}
