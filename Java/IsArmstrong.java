import java.util.Scanner;

public class IsArmstrong {
    public static void main(String[]args){
        Scanner in=new Scanner(System.in);
        int n = in.nextInt();
        System.out.println("Armstrong Status: "+isArmstrong(n));
        
        //printing 3 digit armstrong till 1000
        for (int i=100;i<1000;i++){
            if (isArmstrong(i)){
                System.out.println(i);
            }
        }
        in.close();
    }    

    static boolean isArmstrong(int n){
        int sum = 0;
        int original=n;
        while(n>0){
            int cube=(n%10)*(n%10)*(n%10);
            sum += cube;
            n=n/10;
        }
        if (sum==original){
            return true;
        }
        else{
            return false;
        }
    }
}

