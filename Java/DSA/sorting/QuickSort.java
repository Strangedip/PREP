package DSA.sorting;

import java.util.Arrays;

public class QuickSort {
    public static void main(String[] args) {
        int[] a = { 2, 1, 4, 5, 0, 3 };
        quickSort(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
    }

    public static void quickSort(int[] a, int low, int high) {

        int piviot = a[low];
        int left = low;
        int right = high;
        quickSort(a, low, right);
        while (left <= right) {
            if (a[left] <= piviot) {
                left++;
            } else if (a[left] > piviot) {
                swap(a, left, right);
            }
            if (a[right] >= piviot) {
                right--;
            } else {
                swap(a, left, right);
            }
        }
    }

    public static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
