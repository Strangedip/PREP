package DSA.sorting;
import java.util.Arrays;

public class CyclicSort {
    public static void main(String[] args) {
        int[] a = { 1,3,2,5,4,0};
        cyclicSort(a);
        System.out.println(Arrays.toString(a));
    }

    static void cyclicSort(int[] a) {
        int i = 0;
        while (i < a.length) {
            System.out.println(Arrays.toString(a));
            if (a[i] < a.length && a[i] != a[a[i]]) {
                int temp = a[a[i]];
                a[a[i]] = a[i];
                a[i] = temp;
            } else {
                i++;
            }
        }
    }
}
