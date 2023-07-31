// find missing number in an ranged array 0 to n of size n-1
public class LeetCode268 {
    public static void main(String[] args) {
        int[] a = {0, 5, 3, 1, 2, 6, 4 };
        System.out.println(missingNumber(a));
    }

    public static int missingNumber(int[] nums) {
        cyclicSort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (i != nums[i]) {
                return i;
            }
        }
        return nums.length;
    }

    static void cyclicSort(int[] a) {
        // start with 0th index
        int i = 0;
        // i should be less than size of array and remain same till no swap is required
        while (i < a.length) {
            // the element should be less than size of the array else it will throw error
            // if element-1 is not equal to element index swap element to its n-1 position
            if (a[i] < a.length && i != a[i]) {
                int temp = a[a[i]];
                a[a[i]] = a[i];
                a[i] = temp;
            } else {
                // if element is at right position increase i by one (no swap required)
                i++;
            }
        }
    }
}
