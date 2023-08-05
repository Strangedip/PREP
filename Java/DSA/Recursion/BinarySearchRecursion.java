package DSA.Recursion;

public class BinarySearchRecursion {
    public static void main(String[] args) {
        int[] a = { 1, 4, 6, 7, 8, 9, 11, 15, 19 };
        int key = 11;
        int idx=binarySearchRecursion(0, a.length - 1, a, key);
        System.out.println(idx);

    }

    static int binarySearchRecursion(int start, int end, int[] a, int key) {
        int mid = (start + end)/2;
        if(start>end){
            return -1;
        }
        if (a[mid] == key) {
            return mid;
        } else if (a[mid] > key) {
            return binarySearchRecursion(mid + 1, end, a, key);
        } else {
            return binarySearchRecursion(start, mid + 1, a, key);
        }

    }
}
