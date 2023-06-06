// Create array with index given with elements from given index
import java.util.ArrayList;
import java.util.Arrays;

public class Leetcode1389 {
    public static void main(String[] args) {
        int[] array = { 1, 2, 3, 4, 5 };
        int[] index = { 0, 1, 2, 2, 1 };
        System.out.println(Arrays.toString(createTargetArray1(array, index)));
    }

    public static int[] createTargetArray1(int[] nums, int[] index) {
        // creating listarray so can add at particular index by shifting current value using .add 
        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            array.add(index[i], nums[i]);
        }

        // creating int[] type for ans
        int[] ans = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            ans[i] = array.get(i);
        }
        return ans;
    }
}
