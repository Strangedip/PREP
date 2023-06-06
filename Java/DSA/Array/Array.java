import java.util.Arrays;
import java.util.Scanner;

public class Array {
    public static void main(String[] args){
        Scanner in= new Scanner(System.in);
        int n=in.nextInt();
        int[] array = new int[n];

        // regular for loop
        for (int i =0;i<n;i++){
            array[i]=in.nextInt();
        }

        // for each loop
        for (int i:array){
            System.out.print(i+" ");
        }
        System.out.println("");
        in.close();

        //another way to print an array
        System.out.println(Arrays.toString(array));
    }
}
