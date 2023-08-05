package DSA.Recursion;

public class BinarySearchRecursion {
    public static void main(String[] args) {
        int[] a = { 1, 4, 6, 7, 8, 9, 11, 15, 19 };
        int key = 2;
        int idx = binarySearchRecursion(0, a.length - 1, a, key);
        System.out.println(idx);

    }

    static int binarySearchRecursion(int start, int end, int[] a, int key) {
        int mid = (start + end) / 2;
        if (start > end) {
            // if not found in array return -1
            return -1;
        }
        if (a[mid] == key) {
            // if found return index
            return mid;
        } else if (a[mid] > key) {
            // if key is smaller than mid element reduce end
            return binarySearchRecursion(start, mid - 1, a, key);
        } else {
            // if key is larger than mid element increase start
            return binarySearchRecursion(mid + 1, end, a, key);

        }

    }
}
