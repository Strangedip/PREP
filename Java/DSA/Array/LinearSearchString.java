import java.util.Scanner;
public class LinearSearchString {
    public static void main(String[] args) {
        Scanner in= new Scanner(System.in);
        String array="Sandip";
        char ch= in.next().trim().charAt(0);
        for (char i:array.toCharArray()){
            if (i==ch){
                System.out.println(ch+" found");
            }
        }
        in.close();
    }
}