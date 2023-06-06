public class MinNum {
    public static void main(String[]args){
        int[] array= { 11, 9, 91, -12, 45, 65, 13, 15, 78, 8 };
        minNum(array);
    }

    public static void minNum(int[] array){
        if (array.length==0){
            System.out.println("array size 0");;
        }
        int min=array[0];
        for (int i =1;i<array.length;i++){
            if (array[i]<min){
                min=array[i];
            }
        }
        System.out.println(min);
    }
}
