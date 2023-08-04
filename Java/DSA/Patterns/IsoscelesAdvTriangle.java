package DSA.Patterns;

public class IsoscelesAdvTriangle {
    public static void main(String[] args) {

        starTriangle(5);
        numTriangle(5);
    }

    static void starTriangle(int n) {
        for (int i = 0; i <n; i++) {
            int space = n - i;
            while (space > 0) {
                System.out.print("  ");
                space--;
            }
            int col = i;
            while (col > 0) {
                System.out.print("* ");
                col--;
            }
            int col1 = i-1;
            while (col1 > 0) {
                System.out.print("* ");
                col1--;
            }
            System.out.println();

        }
    }
    static void numTriangle(int n) {
        for (int i = 1; i <=n; i++) {
            int space = n - i;
            while (space > 0) {
                System.out.print("  ");
                space--;
            }
            int col = i;
            while (col > 0) {
                System.out.print(col+" ");
                col--;
            }
            int col1 = 2;
            while (col1 <=i) {
                System.out.print(col1+" ");
                col1++;
            }
            System.out.println();

        }
    }
}
