import java.util.Arrays;

/**
 * Master Theorem — reference for divide-and-conquer recurrence analysis.
 * T(n) = aT(n/b) + f(n) where a >= 1, b > 1
 *
 * Case 1: f(n) = O(n^(log_b(a) - ε)) → T(n) = O(n^(log_b(a)))
 * Case 2: f(n) = O(n^(log_b(a)))       → T(n) = O(n^(log_b(a)) * log n)
 * Case 3: f(n) = Ω(n^(log_b(a) + ε))   → T(n) = O(f(n)) [regularity condition]
 */
public class MasterTheorem {

    /** Merge Sort: T(n) = 2T(n/2) + O(n) → Case 2 → O(n log n) */
    public static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private static void merge(int[] arr, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) {
            temp[k++] = arr[i] <= arr[j] ? arr[i++] : arr[j++];
        }
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];
        for (int idx = 0; idx < temp.length; idx++) arr[left + idx] = temp[idx];
    }

    /** Binary Search: T(n) = T(n/2) + O(1) → Case 2 → O(log n) */
    public static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {38, 27, 43, 3, 9, 82, 10};
        mergeSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
        System.out.println(binarySearch(arr, 27)); // index after sort
    }
}
