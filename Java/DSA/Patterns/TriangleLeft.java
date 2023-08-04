package DSA.Patterns;

public class TriangleLeft {
    public static void main(String[] args) {
        starTriangle(5);
        numTriangle(5);
        reverseTriangle(5);
    }

    static void starTriangle(int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
    static void reverseTriangle(int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n-i; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    static void numTriangle(int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                System.out.print(i+1);
            }
            System.out.println();
        }
    }


}
