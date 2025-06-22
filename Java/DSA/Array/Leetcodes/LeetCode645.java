// You have a set of integers s, which originally contains all the numbers from 1 to n.
// Unfortunately, due to some error, one of the numbers in s got duplicated to another number in the set
// which results in repetition of one number and loss of another number.
// You are given an integer array nums representing the data status of this set after the error.
// Find the number that occurs twice and the number that is missing and return them in the form of an array.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode645 {
 public static void main(String[] args) {
        int[] a = { 1,1};
        cyclicSort(a);
        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(findErrorNums(a)));
    }

    public static int[] findErrorNums(int[] nums) {
        cyclicSort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (i != nums[i] - 1 ) {
                return new int[]{nums[i],i+1};
            }
        }
        return new int[]{nums[nums.length-1],nums.length-1};
    }

    static void cyclicSort(int[] a) {
        // start with 0th index
        int i = 0;
        // i should be less than size of array and remain same till no swap is required
        while (i < a.length) {
            // the element should be less than size of the array else it will throw error
            // if element-1 is not equal to element index swap element to its n-1 position
            if (a[i] < a.length + 1 && a[a[i] - 1] != a[i]) {
                int temp = a[a[i] - 1];
                a[a[i] - 1] = a[i];
                a[i] = temp;
            } else {
                // if element is at right position increase i by one (no swap required)
                i++;
            }
        }
    }
}

// use cyclic sort -> a[i] ~ i
// since it is ranged and have to find missing. use cyclic sort to sort in O(n). then search duplicate using linear search
// as a[i] !~ i, this values is repeated and should have a[i] as ~i
