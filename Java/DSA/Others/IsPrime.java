package DSA.Others;
import java.util.Scanner;

public class IsPrime {
    static public void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        System.out.println("prime status : "+isPrime(n));
        in.close();
    }

    static boolean isPrime(int n) {
        for (int i = 2; i*i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
