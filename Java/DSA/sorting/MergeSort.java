package DSA.sorting;

import java.util.*;

public class MergeSort {
    public static void main(String[] args) {
        int[] a = { 2, 1, 4, 5, 0, 3 };
        mergeSort(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));

    }

    public static void mergeSort(int[] a, int l, int r) {
        if (l >= r) {
            return;
        }
        int mid = (l + r) / 2;
        mergeSort(a, l, mid);
        mergeSort(a, mid + 1, r);
        merge(a, l, mid, r);

    }

    public static void merge(int[] a, int low, int mid, int high) {
        List<Integer> temp = new ArrayList<>();
        int left = low;
        int right = mid + 1;
        while (left <= mid && right <= high) {
            if (a[left] <= a[right]) {
                temp.add(a[left]);
                left++;
            } else {
                temp.add(a[right]);
                right++;
            }
        }
        while (left <= mid) {
            temp.add(a[left]);
            left++;
        }
        while (right <= high) {
            temp.add(a[right]);
            right++;
        }
        for (int i = low; i <= high; i++) {
            a[i] = temp.get(i-low);
        }
    }
}
