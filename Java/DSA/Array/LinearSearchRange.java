
import java.util.Scanner;

public class LinearSearchRange {
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        int[] array = { 1, 9, 91, 12, 45, 65, 13, 15, 78, 8 };
        System.out.print("Target : ");
        int n = in.nextInt();
        System.out.print("range : ");
        int a = in.nextInt();
        int b = in.nextInt();
        System.out.println(rangeSearch(array, n, a, b));
        in.close();
    }

    public static boolean rangeSearch(int[] array, int target, int a, int b) {

        while (a < b) {
            if (array[a] == target) {
                return true;
            } else {
                a++;
            }
        }
        return false;
    }
}
