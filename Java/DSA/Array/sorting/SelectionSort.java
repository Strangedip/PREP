import java.util.Arrays;

public class SelectionSort {
    static int count1 = 0;
    static int count2 = 0;

    // ignore counters.. its just to count checks in algorithm
    public static void main(String[] args) {
        // Best case
        // int[] a = { 1, 2, 33, 55, 77, 100, 110, 200 };
        // int[] b = { 1, 2, 33, 55, 77, 100, 110, 200 };

        // avg case
        // int[] a = { 4, 2, 0, 7, 1, 12, -3, -12 };
        // int[] b = { 4, 2, 0, 7, 1, 12, -3, -12 };

        // worst case
        int[] a = { 5, 4, 3, 2, 1, 0, -1, -10 };
        int[] b = { 5, 4, 3, 2, 1, 0, -1, -10 };

        System.out.println(Arrays.toString(a));
        selectionSort(a);
        System.out.println(Arrays.toString(a));
        System.out.println("no of checks: " + count1);

        // my modification
        modifiedSelectionSort(b);
        System.out.println(Arrays.toString(b));
        System.out.println("no of checks: " + count2);
    }

    static void selectionSort(int[] a) {
        // fix array size as end index to reduce as sorted array index
        int end = a.length - 1;
        while (end > 0) {
            count1++;
            int max = max(a, end);

            // max element is already at the end of unsorted sub array don't swap
            if (max == end) {
                end--;
                continue;
            }
            count1 += end;

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

    // selecting next element and checking its position in array
    // by comparing with all previous values
    static void modifiedSelectionSort(int[] a) {
        // starting from index 1
        for (int i = 1; i < a.length; i++) {
            count2++;
            // continue if next element is already bigger than end of sorted array
            if (a[i - 1] < a[i]) {
                continue;
            }

            for (int j = 0; j < i; j++) {
                count2++;
                // else swap with bare smaller element
                if (a[i] < a[j]) {
                    int temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }

}
