// concatinate given array twice in a new array

import java.util.Arrays;

public class Leetcode1929 {
    public static void main(String[] args) {
        int[] array = { 1, 2, 3, 1 };
        System.out.println(Arrays.toString(getConcatenation(array)));
    }

    public static int[] getConcatenation(int[] nums) {
        int[] ans = new int[2 * (nums.length)];
        int index = 0;
        byte count = 2;
        while (index < ans.length && count > 0) {
            for (int i = 0; i < nums.length; i++) {
                ans[index]=nums[i];
                index++;
            }
            count--;
        }
        return ans;
    }

}
