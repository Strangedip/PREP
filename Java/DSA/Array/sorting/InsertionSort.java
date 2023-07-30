import java.util.Arrays;

public class InsertionSort {

    static int count = 0;

    // ignore counters.. its just to count checks in algorithm
    public static void main(String[] args) {
        // Best case
        // int[] a = { 1, 2, 33, 55, 77, 100, 110, 200 };

        // avg case
        int[] a = { 4, 2, 0, 7, 1, 12, -3, -12 };

        // worst case
        // int[] a = { 5, 4, 3, 2, 1, 0, -1, -10 };

        System.out.println(Arrays.toString(a));
        insertionSort(a);
        // modifiedInsertionSort(a);
        System.out.println(Arrays.toString(a));
        System.out.println("no of checks: " + count);

    }

    static void insertionSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            count++;

            // check if already at right position
            if (a[i + 1] > a[i]) {
                continue;
            }
            for (int j = i + 1; j > 0; j--) {
                count++;
                // if previous element is bigger swap with bigger element
                if (a[j] < a[j - 1]) {
                    int temp = a[j - 1];
                    a[j - 1] = a[j];
                    a[j] = temp;
                }
                // if previous element is smaller or equal break loop as its already sorted
                else {
                    break;
                }
            }
        }
    }

    // selecting next element and checking its position in array
    // by comparing with all previous values
    static void modifiedInsertionSort(int[] a) {
        // starting from index 1
        for (int i = 1; i < a.length; i++) {
            count++;
            // continue if next element is already bigger than end of sorted array
            if (a[i - 1] < a[i]) {
                continue;
            }
            for (int j = 0; j < i; j++) {
                count++;
                // else swap with each smaller element
                if (a[i] < a[j]) {
                    int temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }
}
