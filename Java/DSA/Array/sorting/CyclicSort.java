import java.util.Arrays;

public class CyclicSort {
    public static void main(String[] args) {
        int[] a = { 0, 2, 3, 5, 4 };
        cyclicSort(a);
        System.out.println(Arrays.toString(a));
    }

    static void cyclicSort(int[] a) {
        int i = 0;
        while (i < a.length) {
            System.out.println(Arrays.toString(a));
            if (a[i] < a.length && a[i] != i) {
                int temp = a[a[i]];
                a[a[i]] = a[i];
                a[i] = temp;
            } else {
                i++;
            }
        }
    }
}
