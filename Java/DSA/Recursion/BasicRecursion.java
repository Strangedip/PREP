package DSA.Recursion;

public class BasicRecursion {
    public static void main(String[] args) {
        num(1);

    }

    static void num(int n) {
        // OR
        // if(n>5){
        // return;
        // }
        System.out.println(n);
        if (n == 5) {
            return;
        }
        // always call method recursively only after checking certain BASE (breaking) condition
        num(n + 1);
    }
}
