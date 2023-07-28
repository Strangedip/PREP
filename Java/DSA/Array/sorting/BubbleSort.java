import java.util.Arrays;

public class BubbleSort {
    public static void main(String[] args) {
        int[] a = { 4, 2, 0, 7, 1, 12, -3, -12 };

        // print before sorting
        System.out.println(Arrays.toString(a));
        // sort current array
        // bubbleSort(a);

        // print after sorting array using Arrays class
        // System.out.println(Arrays.toString(a));

        bubbleSort1(a);
        System.out.println(Arrays.toString(a));
    }

    // comparing one with next, if larger swap without changing index (takes 35 turns)
    static void bubbleSort(int[] a) {
        // for (int i = 0; i < a.length - 1; i++)
        int i = 0;
        // i will run for n-1
        while (i < a.length - 1) {
            int j = i + 1;

            // j will run for n
            while (j < a.length) {
                System.out.println(Arrays.toString(a));
                // check if next number is greater then exchange with current number
                if (a[i] > a[j]) {
                    int temp = a[j];
                    a[j] = a[i];
                    a[i] = temp;
                }
                j++;
            }
            i++;
        }
    }

    // comparing one element with rest of the array and swap if current is larger
    // and change current index to swapped larger elemnet takes(57 turns)
    static void bubbleSort1(int[] a) {
        while (true) {
            boolean flag = true;
            int i = 0;
            int j = 1;
            // check if array is sorted
            for (int i2 = 0; i2 < a.length - 1; i2++) {
                if (!(a[i2] < a[i2 + 1])) {
                    flag = false;
                }
            }
            if (flag) {
                break;
            } else {
                while (i < a.length - 1 && j < a.length) {
                    System.out.println(Arrays.toString(a));
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

    }
}