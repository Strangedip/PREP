
// Binary search for both orders ascending and descending arrays
public class BinarySearchOrderAgnostic {
    public static void main(String[] args) {
        // acsending array
        int[] ascArray = { -12, -5, 0, 12, 30, 33, 35, 45, 49, 87, 99 };

        // decsending array
        int[] desArray = { 99, 87, 49, 45, 33, 35, 30, 12, 0, -5, -12 };

        int target = -12;

        System.out.println(binarySearchInOrderAgnostic(ascArray, target));
        System.out.println(binarySearchInOrderAgnostic(desArray, target));

    }

    public static int binarySearchInOrderAgnostic(int[] array, int target) {
        int start = 0;
        int end = array.length - 1;

        // check if array is ascending or descending
        boolean orderAsc = array[start] < array[end];

        while (start <= end) {
            int middle = (start + end) / 2;

            // if target is at middle return middle index
            if (array[middle] == target) {
                return middle;
            }
            // else check if array is acending or dexending and perform binary search
            else {
                if (orderAsc) {
                    if (target > array[middle]) {
                        start = middle + 1;
                    } else {
                        end = middle - 1;
                    }
                } else {
                    if (target < array[middle]) {
                        start = middle + 1;
                    } else {
                        end = middle - 1;
                    }
                }
            }
        }
        return -1;
    }
}
