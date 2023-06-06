import java.util.Scanner;

public class ExpressionCalculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        double ans = 0;
        while (true) {
            try {
                System.out.print("press ctrl+c to close");
                System.out.print("Enter expression eg. 1 + 2 : ");
                double num1 = in.nextDouble();
                int operator = in.next().trim().charAt(0);
                double num2 = in.nextDouble();

                // can us switch for one liner  as 
                // switch(var):
                //      case -> statement;

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
                    default:
                        System.out.println("invalid operator");
                        break;
                }
                System.out.println(ans);

            } catch (Exception e) {
                System.out.println("Error or incorrect format closing calculator");
                break;
            }
        }
        in.close();
    }
}
