import java.util.Arrays;

public class BubbleSort {
    public static void main(String[] args) {
        // Best cases
        // int[] a = { 4, 5, 7, 10, 20, 30, 112 };
        // int[] b = { 4, 5, 7, 10, 20, 30, 112 };
        // int[] c = { 4, 5, 7, 10, 20, 30, 112 };

        // // Avg cases
        int[] a = { 4, 2, 0, 7, 1, 12, -3, -12 };
        int[] b = { 4, 2, 0, 7, 1, 12, -3, -12 };
        int[] c = { 4, 2, 0, 7, 1, 12, -3, -12 };

        // Worst case
        // int[] a = { 7, 6, 5, 4, 3, 2, 1 };

        // print before sorting
        System.out.println(Arrays.toString(a));
        System.out.println("\nactual bubble sorting algorithm");
        bubbleSort(a);
        System.out.println(Arrays.toString(a));

        System.out.println("\nbubble sort with while");
        bubbleSort1(b);
        System.out.println(Arrays.toString(b));

        System.out.println("\nbubble sort with index change");
        bubbleSort2(c);
        System.out.println(Arrays.toString(c));

    }

    // actual bubble sorting algorithm
    static void bubbleSort(int[] a) {
        // keeps counter
        int count = 0;
        
        for (int i = 0; i < a.length ; i++) {
            count++;
            // assuming array is sorted
            boolean flag = true;

            // iterates over array for j and j-1 values
            // don't check for already moved at end element hence a.length-i (i is counter for numbers moved)
            for (int j = 1; j < a.length - i; j++) {
                count++;

                // if j-1 > j swap
                if (a[j - 1] > a[j]) {
                    int temp = a[j];
                    a[j] = a[j - 1];
                    a[j - 1] = temp;

                    // mark as unsorted
                    flag = false;
                }
            }
            // if array is already sorted stop loop
            if (flag) {
                break;
            }

        }
        System.out.println("count " + count);
    }

    // comparing one with next, if larger swap without changing index using while
    // loop
    static void bubbleSort1(int[] a) {
        // for (int i = 0; i < a.length - 1; i++)

        int count = 0;
        int i = 0;
        // i will run for n-1
        while (i < a.length - 1) {
            // flag check is swapping is performed or not
            // if no swap done means array is sorted and break loop
            boolean flag = true;
            count++;
            int j = i + 1;

            // j will run for n
            while (j < a.length) {
                count++;
                // check if next number is greater then exchange with current number
                if (a[i] > a[j]) {
                    int temp = a[j];
                    a[j] = a[i];
                    a[i] = temp;
                    flag = false;
                }
                j++;
            }
            i++;
            if (flag) {
                break;
            }
        }
        System.out.println("count " + count);
    }

    // comparing one element with rest of the array and swap if current is larger
    // and change current index to swapped larger element takes(57 turns)
    static void bubbleSort2(int[] a) {
        int count = 0;
        while (true) {
            count++;
            boolean flag = true;
            int i = 0;
            int j = 1;
            // check if array is sorted separately
            for (int i2 = 0; i2 < a.length - 1; i2++) {
                // count++;
                if (!(a[i2] < a[i2 + 1])) {
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
            // if not sorted perform sorting
            else {
                while (i < a.length - 1 && j < a.length) {
                    count++;
                    if (a[i] > a[j]) {
                        int temp = a[j];
                        a[j] = a[i];
                        a[i] = temp;
                        i = j;
                        j++;
                    } else if (a[i] < a[j]) {
                        i = j;
                        j++;
                    } else {
                        i++;
                        j++;
                    }
                }
            }
        }
        System.out.println("count " + count);

    }
}