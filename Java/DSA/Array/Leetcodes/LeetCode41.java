// Given an unsorted integer array nums, return the smallest missing positive integer.
// You must implement an algorithm that runs in O(n) time and uses O(1) auxiliary space.


public class LeetCode41 {
    public static void main(String[] args) {
        int[] a = {  1 };
        System.out.println(firstMissingPositive(a));
    }

    static int firstMissingPositive(int[] nums) {
        cyclicSort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (nums[i]!=i+1) {
                return i+1;
            }
        }
        return nums.length+1;
    }
    static void cyclicSort(int[] a) {
        int i = 0;
        while (i < a.length) {
            if (a[i] > 0 && a[i] <= a.length && a[i] != a[a[i]-1]) {
                int temp = a[a[i]-1];
                a[a[i]-1] = a[i]; 
                a[i] = temp;
            } else {
                i++;
            }
        }
    }
}
