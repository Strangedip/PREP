package DSA.Patterns;

public class Arrow {
    public static void main(String[] args) {
        arrow(10);
        arrow1(10);
        arrow3(5);
    }

    static void arrow(int n) {
        for (int i = 1; i < n / 2; i++) {
            int j = 1;
            while (j <= i) {
                System.out.print("* ");
                j++;
            }
            System.out.println();
        }

        for (int i = n / 2; i >= 0; i--) {
            int j = i-1;
            while (j >= 0) {
                System.out.print("* ");
                j--;
            }
            System.out.println();
        }
    }

    static void arrow1(int n) {
        for (int i = 1; i < n; i++) {
            if (i < (n / 2)) {
                int j = 0;
                while (j < i) {
                    System.out.print("* ");
                    j++;
                }
                System.out.println();
            } else {
                int j = n - i;
                while (j > 0) {
                    System.out.print("* ");
                    j--;
                }
                System.out.println();
            }
        }
    }

    static void arrow3(int n) {
        for (int row = 0; row < 2 * n; row++) {
            int col = (row > n ? 2 * n - row : row);
            for (int j = 0; j < col; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}
