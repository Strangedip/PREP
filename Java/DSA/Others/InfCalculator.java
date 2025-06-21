package DSA.Others;
import java.util.Scanner;

public class InfCalculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter Num1 : ");
        int num1 = in.nextInt();

        System.out.println("enter q to close");
        System.out.print("Enter operator : ");
        int operator = in.next().trim().charAt(0);

        int ans = 0;
        while (operator != 'q') {

            System.out.print("Enter next Num :");
            int num2 = in.nextInt();

            switch (operator) {
                case '+':
                    ans = num1 + num2;
                    break;
                case '-':
                    ans = num1 - num2;
                    break;
                case '*':
                    ans = num1 * num2;
                    break;
                case '/':
                    ans = num1 / num2;
                    break;
            }
            System.out.println("ans = "+ans);
            num1 = ans;
            System.out.println("enter q to close");
            System.out.print("Enter next operator : ");
            operator = in.next().trim().charAt(0);
        }
        in.close();

    }
}
