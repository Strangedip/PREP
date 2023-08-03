package DSA.Patterns;

public class TriangleRight {
    public static void main(String[] args) {
        int n=5;
        for (int i = 0; i < n; i++) {
            int k=n-1-i;
                while(k>0){
                    System.out.print(" ");
                    k--;
                }
            for (int j = 0; j <=i; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
