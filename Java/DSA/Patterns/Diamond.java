package DSA.Patterns;

public class Diamond {
    public static void main(String[] args) {
        starDiamind(5);
        numDiamind(5);
        alphaDiamind(5);
    }

    static void starDiamind(int n) {
        for (int i = 0; i < 2 * n; i++) {
            int space = i > n ? i : 2 * n - i;
            for (int j = 0; j < space; j++) {
                System.out.print(" ");
            }

            int col = i > n ? 2 * n - i : i;
            for (int j = 0; j < col; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
    static void numDiamind(int n) {
        for (int i = 0; i < 2 * n; i++) {
            int space = i > n ? i : 2 * n - i;
            for (int j = 0; j < space; j++) {
                System.out.print(" ");
            }

            int col = i > n ? 2 * n - i : i;
            for (int j = 0; j < col; j++) {
                System.out.print(col+" ");
            }
            System.out.println();
        }
    }
    static void alphaDiamind(int n) {
        for (int i = 0; i < 2 * n; i++) {
            int space = i > n ? i : 2 * n - i;
            for (int j = 0; j < space; j++) {
                System.out.print(" ");
            }

            int col = i > n ? 2 * n - i : i;
            for (int j = 0; j < col; j++) {
                System.out.print((char)(64+col)+" ");
            }
            System.out.println();
        }
    }

}
