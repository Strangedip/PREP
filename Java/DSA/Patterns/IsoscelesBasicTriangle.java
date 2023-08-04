package DSA.Patterns;

public class IsoscelesBasicTriangle {
    public static void main(String[] args) {
        starTriangle(5);
        reverseTriangle(5);
        numTriangle(5);
    }

    static void starTriangle(int n) {
        for (int i = 0; i < n; i++) {
            int j = (n - i);
            while (j > 1) {
                System.out.print(" ");
                j--;
            }
            int k = i + 1;
            while (k > 0) {
                System.out.print("* ");
                k--;
            }
            int l = (n - i);
            while (l > 1) {
                System.out.print(" ");
                l--;
            }
            System.out.println();

        }
    }

    static void reverseTriangle(int n) {
        for (int i = 0; i < n; i++) {
            int j = i;
            while (j > 0) {
                System.out.print(" ");
                j--;
            }
            int k = n-i;
            while (k > 0) {
                System.out.print("* ");
                k--;
            }
            int l = i;
            while (l > 0) {
                System.out.print(" ");
                l--;
            }
            System.out.println();

        }
    }

    static void numTriangle(int n) {
        for (int i = 0; i < n; i++) {
            int j = (n - i);
            while (j > 1) {
                System.out.print(" ");
                j--;
            }
            int k = i + 1;
            while (k > 0) {
                System.out.print(i + 1 + " ");
                k--;
            }
            int l = (n - i);
            while (l > 1) {
                System.out.print(" ");
                l--;
            }
            System.out.println();

        }
    }
}
