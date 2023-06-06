
import java.util.Scanner;

public class LinearSearch {
    public static void main(String[] args) {
        Scanner in= new Scanner(System.in);
        int[] array={1,3,5,9,44,12,10,13,2,4,6};
        int target=in.nextInt();
        for (int i:array){
            if (i==target){
                System.out.println("Found "+i);
            }
        }

        for (int i =0;i<array.length;i++){
            if (array[i]==target){
                System.out.println("Found "+target+" at index "+i);
            }
        }
        System.out.println();
        in.close();
    }
}
