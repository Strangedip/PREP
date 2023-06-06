import java.util.Scanner;

public class NumberCount {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        long n = in.nextLong();
        int target = in.nextInt();
        int count = 0;
        while (n > 0) {
            long remainder = n % 10;
            if (remainder == target) {
                count++;
            }
            n = n / 10;
        }
        System.out.println(count);
        in.close();

    }
}
