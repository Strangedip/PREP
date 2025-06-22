// find the 1st and last index of element in a sorted array
// Input: nums = [5,7,7,8,8,10], target = 8
// Output: [3,4]

import java.util.Arrays;

public class Leetcode43 {
    public static void main(String[] args) {
        int[] nums = { 1, 2, 3 };
        int target = 2;
        int[] ans = searchRange(nums, target);
        System.out.println(Arrays.toString(ans));

        //extra function
        int[] newAns = {index(nums, target, true),index(nums, target, false)};
        System.out.println(Arrays.toString(newAns));
    }

    public static int[] searchRange(int[] nums, int target) {
        return new int[] { startIndex(nums, target), endIndex(nums, target) };
    }

    public static int startIndex(int[] nums, int target) {
        int start = 0;
        int end = nums.length - 1;
        int middle;
        int ans = -1;
        while (start <= end) {
            middle = (start + end) / 2;
            if (nums[middle] == target) {
                end = middle - 1;
                ans = middle;
            } else if (nums[middle] > target) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        return ans;
    }

    public static int endIndex(int[] nums, int target) {
        int start = 0;
        int end = nums.length - 1;
        int middle;
        int ans = -1;
        while (start <= end) {
            middle = (start + end) / 2;
            if (nums[middle] == target) {
                start = middle + 1;
                ans = middle;
            } else if (nums[middle] > target) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        return ans;
    }

    // extra fucntion to find both start and end index with a boolean according to value true or false
    public static int index(int[] nums, int target, boolean findStart) {
        int start = 0;
        int end = nums.length - 1;
        int middle;
        int ans = -1;
        while (start <= end) {
            middle = (start + end) / 2;
            if (nums[middle] == target) {
                ans = middle;
                if (findStart) {
                    start = middle + 1;
                }
                else{
                    end=middle-1;
                }
                
            } else if (nums[middle] > target) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        return ans;
    }
}

// Uses binary search, find recurring number start finding its start and end.