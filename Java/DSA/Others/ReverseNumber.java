package DSA.Others;
import java.util.Scanner;

public class ReverseNumber{
    public static void main(String[] args){
        Scanner in= new Scanner(System.in);
        int n = in.nextInt();
        int reverse=0;
        int digit=0;
        while (n>0){
            digit= n%10;
            reverse= reverse*10+digit;
            n=n/10;
        }
        System.out.println(reverse);
        in.close();
    }
}
