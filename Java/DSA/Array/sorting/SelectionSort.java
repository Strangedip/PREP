import java.util.Arrays;

public class SelectionSort {
    public static void main(String[] args) {
        // avg case
        int[] a = { 4, 2, 0, 7, 1, 12, -3, -12 };

        // worst case
        // int[] a = { 5, 4, 3, 2, 1, 0 };

        System.out.println(Arrays.toString(a));
        selectionSort(a);
        System.out.println(Arrays.toString(a));
    }

    static void selectionSort(int[] a) {
        // fix array size as end index to reduce as sorted array index
        int end = a.length - 1;
        while (end > 0) {
            int max = max(a, end);
            // max element is already at the end of unsorted sub array don't swap
            if (max == end) {
                end--;
                continue;
            }

            // swapping max element index with end index of unsorted array
            int temp = a[max];
            a[max] = a[end];
            a[end] = temp;
            end--;
        }
    }

    // find max element index in array till the end
    static int max(int[] a, int end) {
        int max = 0;
        for (int i = 1; i <= end; i++) {
            if (a[i] > a[max]) {
                max = i;
            }
        }
        return max;
    }

}
