// print index of ceiling number i.e greater than target but (smallest in array) or equal to target

// print index of floor number i.e smaller than target but (biggest in array) or equal to target

public class Q1CeilingAndFloorOfNumber {
    public static void main(String[] args) {
        int[] array = { -1, 3, 5, 8, 7, 9, 11, 13, 17, 20, 22, 25, 27 };
        int target = 18;
        System.out.println(floorNumber(array, target));
        System.out.println(ceilingNumber(array, target));


    }

    public static int ceilingNumber(int[] array, int target) {
        int start = 0;
        int end = array.length - 1;
        int middle;
        // int ans=target;
        while (end >= start) {
            middle = (start + end) / 2;
            if (array[middle] == target) {
                return middle;
            } else if (array[middle] < target) {
                start = middle + 1;
            } else if (array[middle] > target) {
                end = middle - 1;
            }
        }
        // edge case if greater or equal to tarhet elemnt is not present
        if (start > array.length -1) {
            return -1;
        }
        return start;
    }

    public static int floorNumber(int[] array, int target) {
        int start = 0;
        int end = array.length - 1;
        int middle;
        // int ans=target;
        while (end >= start) {
            middle = (start + end) / 2;
            if (array[middle] == target) {
                return middle;
            } else if (array[middle] < target) {
                start = middle + 1;
            } else if (array[middle] > target) {
                end = middle - 1;
            }
        }
        // edge case if greater or equal to tarhet elemnt is not present
        if (end < 0) {
            return -1;
        }
        return end;
    }
    
}
