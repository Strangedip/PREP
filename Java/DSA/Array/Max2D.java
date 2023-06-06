import java.util.Arrays;

public class Max2D {
    public static void main(String[] args) {
        int[][] array = { { 11, 9, 91 }, { -12, 45, 65, 93 }, { 15, 78, 8 } };
        System.out.println(max2D(array));

        int[] indexes= indexMax2D(array);
        System.out.println(Arrays.toString(indexes));

    }

    static int max2D(int[][] array) {
        int max = array[0][0];
        for (int[] rowArr : array) {
            for (int element : rowArr) {
                if (element > max) {
                    max = element;
                }
            }
        }
        return max;
    }

    static int[] indexMax2D(int[][] array){
        int[] index={0,0};
        int max=array[0][0];
        for(int i=0;i<array.length;i++){
            for (int j=0;j<array[i].length;j++){
                if (array[i][j]>max){
                    max=array[i][j];
                    index[0]=i;
                    index[1]=j;
                }
            }
        }
        return index;
    }
}
