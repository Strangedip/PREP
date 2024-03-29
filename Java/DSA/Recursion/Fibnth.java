package DSA.Recursion;

public class Fibnth {
    public static void main(String[] args) {
        System.out.println(fibNth(5));
        fibPrint(0, 1, 5);
    }

    static int fibNth(int n) {
        // if (n == 0) {
        // return 0;
        // }
        // if (n == 1) {
        // return 1;
        // }
        // OR
        if (n < 2) {
            return n;
        }
        return fibNth(n - 1) + fibNth(n - 2); // not a tail recursion
    }


    static void fibPrint(int f, int s, int n) {
        if (n < 0) {
            return;
        }
        System.out.println(f);
        fibPrint(s, f + s, --n);
    }
}
