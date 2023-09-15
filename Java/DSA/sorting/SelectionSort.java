package DSA.sorting;

import java.util.Arrays;

public class SelectionSort {

    // ignore counters.. its just to count checks in algorithm
    public static void main(String[] args) {
        // Best case
        // int[] a = { 1, 2, 33, 55, 77, 100, 110, 200 };

        // avg case
        // int[] a = { 4, 2, 0, 7, 1, 12, -3, -12 };;

        // worst case
        int[] a = { 5, 4, 3, 2, 1, 0, -1, -10 };

        System.out.println(Arrays.toString(a));
        selectionSort(a);
        System.out.println(Arrays.toString(a));

    }

    static void selectionSort1(int[] a) {
        for (int i = 0; i < a.length; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[i] > a[j]) {
                    min = j;
                }
            }
            // swapping min element index with current element
            if (min != i) {
                int temp = a[i];
                a[i] = a[min];
                a[min] = temp;
            }
        }
    }

    static void selectionSort(int[] a) {
        // fix array size as end index to reduce as sorted array index
        int end = a.length - 1;
        while (end > 0) {
            int max = max(a, end);

            // max element is already at the end of unsorted sub array don't swap
            if (max != end) {
                // swapping max element index with end index of unsorted arrayF
                int temp = a[max];
                a[max] = a[end];
                a[end] = temp;
            }
            end--;
        }
    }

    // find max element index in array till the end
    static int max(int[] a, int end) {
        int max = 0;
        for (int i = 0; i <= end; i++) {
            if (a[i] > a[max]) {
                max = i;
            }
        }
        return max;
    }

}
