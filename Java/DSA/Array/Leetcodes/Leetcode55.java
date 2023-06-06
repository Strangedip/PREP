// **************************
// jump from 1 index to other depending on value of index user currently is 
// check if user can reaach last element (max jump<=array[i])

public class Leetcode55 {
    public static void main(String[] args) {
        int[] array = {2,0,2,4};
        System.out.println(jump(array));
    }

    // dynamic programming approach (copied)
    public static boolean jump(int[] nums) {
        int boundary = 0;
        for (int i = 0; i <= boundary; i++) {
            boundary = Math.max(boundary, i + nums[i]);
            if (boundary >= nums.length - 1)
                return true;
        }
        return false;
    }
}
