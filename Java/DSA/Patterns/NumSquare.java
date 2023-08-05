package DSA.Patterns;

public class NumSquare {
    public static void main(String[] args) {
        numSquare1(4);
        System.out.println();
        numSquare2(4);
    }

    static void numSquare1(int n) {
        for (int row = 1; row < 2 * n; row++) {

            for (int col = 1; col < 2 * n; col++) {
                // get the maximum (n + 1 - minimum) distance from all walls for 2*n (max upto
                // n)
                int num = (n + 1) - Math.min(Math.min(row, col), Math.min(2 * n - row, 2 * n - col));
                System.out.print(num+" ");
            }
            System.out.println();

        }
    }

    static void numSquare2(int n) {
        for (int row = 1; row < 2 * n; row++) {

            for (int col = 1; col < 2 * n; col++) {
                // get the minimum distance from all walls for 2*n (minimum from all direction)
                int num = Math.min(Math.min(row, col), Math.min(2 * n - row, 2 * n - col));
                System.out.print(num+" ");
            }
            System.out.println();

        }
    }

}
