package DSA.Patterns;

public class Arrow {
    public static void main(String[] args) {
        arrow1(10);
    }

    static void arrow(int n) {
        for (int i = 1; i <= n/2; i++) {
            int j = 1;
            while (j <= i) {
                System.out.print("*");
                j++;
            }
            System.out.println();
        }
        
        for (int i = n/2; i >=0; i--) {
            int j = i;
            while (j >=0) {
                System.out.print("*");
                j--;
            }
            System.out.println();
        }
    }

    static void arrow1(int n){
        for (int i = 1; i < n; i++) {
            if(i<(n/2)){
                int j=0;
                while(j<i){
                    System.out.print("*");
                    j++;
                }
                System.out.println();
            }
            else{
                int j=n-i;
                while(j>0){
                    System.out.print("*");
                    j--;
                }
                System.out.println();
            }
        }
    }
}
