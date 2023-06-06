public class BinarySearch {
    public static void main(String[] args) {
        int[] array = new int[100000];
        for (int i = 0; i < 100000; i++) {
            array[i] = i + 1;
        }
        int target = 12345;
        System.out.println(binarySearch(array, target));
    }

    public static int binarySearch(int[] array, int target) {
        int start = 0;
        int end = array.length - 1;
        int count = 0;
        while (start <= end) {
            int middle = (start + end) / 2;
            if (array[middle] == target) {
                return middle;
            } else if (array[middle] > target) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
            count++;
            System.out.println("#" + count);
            System.out.println("S-" + start);
            System.out.println("E-" + end);
            
        }
        return -1;
    }
}
