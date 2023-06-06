import java.util.Arrays;
import java.util.Scanner;

public class Search2D {
    public static void main(String[]args){
        int[][] array= {
            {1,2,3},
            {4,5},
            {6,7,8,9}
        };
        Scanner in=new Scanner(System.in);
        int n= in.nextInt();
        System.out.println(search2D(array, n));

        int[] indexes=indexSearch2D(array, n);
        System.out.println(Arrays.toString(indexes));
        in.close();
    }

    //returning boolean true if element found
    static boolean search2D(int[][] array,int n){
        for (int i=0;i<array.length;i++){
            for (int j=0;j<array[i].length;j++){
                if (array[i][j]==n){
                    return true;
                }
            }
        }
        return false;
    }

    //returning indexes as int array
    static int[] indexSearch2D(int[][] array,int n){
        int[] index={-1,-1};
        for (int i=0;i<array.length;i++){
            for (int j=0;j<array[i].length;j++){
                if (array[i][j]==n){
                    index[0]=i;
                    index[1]=j;
                }
            }
        }
        return index;
    }
}
