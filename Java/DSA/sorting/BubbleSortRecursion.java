package DSA.sorting;

import java.util.Arrays;

public class BubbleSortRecursion {
    public static void main(String[] args) {
        int[] a = { 2, 1, 4, 5, 0, 3 };
        bubbleSort(a, a.length-1);
        System.out.println(Arrays.toString(a));
    }
    public static void bubbleSort(int[]a,int n)
    {
        if(n<2){
            return;
        }
        for (int i = 0; i < n; i++) {
            if(a[i]>a[i+1]){
                int temp=a[i];
                a[i]=a[i+1];
                a[i+1]=temp;
            }
        }
        bubbleSort(a, --n);
    }
}
