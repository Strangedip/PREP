package DSA.Recursion;

public class Fibnth {
    public static void main(String[] args) {
        System.out.println(fibNth(6));
    }
    static int fibNth(int n){
        if(n==0){
            return 0;
        }
        if(n==1){
            return 1;
        }
        return fibNth(n-1)+fibNth(n-2);
    }
}