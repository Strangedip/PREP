import java.util.Arrays;

public class CyclicSort {

    // cyclic sort is used when data is in the range with all the values of the
    // range
    // e.g 1,2,3,4...N (sorted)
    public static void main(String[] args) {
        // range 1 to 5
        int[] a = { 5, 3, 1, 2, 6, 4 };

        System.out.println(Arrays.toString(a));
        cyclicSort(a);
        System.out.println(Arrays.toString(a));
    }


    static void cyclicSort(int[] a) {
        // start with 0th index
        int i = 0;
        // i should be less than size of array and remain same till no swap is required
        while (i < a.length) {
            // if element-1 is not equal to element index swap element to its n-1 position
            if (i != a[i] - 1) {
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
