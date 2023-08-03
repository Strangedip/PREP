package DSA.Patterns;

public class Rectangle {
    public static void main(String[] args) {
        int l=5;
        int b=6;
        for (int i = 0; i < b; i++) {
            for (int j = 0; j < l; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}
