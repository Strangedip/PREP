package DSA.Patterns;

public class TriangleRight {
    public static void main(String[] args) {
        starTriangle(5);
        numTriangle(5);
        reverseTriangle(5);
    }

    static void starTriangle(int n) {
        for (int i = 0; i < n; i++) {
            int k = n - 1 - i;
            while (k > 0) {
                System.out.print(" ");
                k--;
            }
            for (int j = 0; j <= i; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
    static void reverseTriangle(int n) {
        for (int i = 0; i < n; i++) {
            int k = i;
            while (k > 0) {
                System.out.print(" ");
                k--;
            }
            for (int j = 0; j < n-i; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    static void numTriangle(int n) {
        for (int i = 0; i < n; i++) {
            int k = n - 1 - i;
            while (k > 0) {
                System.out.print(" ");
                k--;
            }
            for (int j = 0; j <= i; j++) {
                System.out.print(i + 1);
            }
            System.out.println();
        }
    }
}
