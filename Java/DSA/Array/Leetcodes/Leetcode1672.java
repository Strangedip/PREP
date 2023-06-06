
// find richest in users with balance they have
public class Leetcode1672 {
    public static void main(String[] args) {
        int[][] array={{1,2,3},{3,2,1},{3,2,2}};
        System.out.println(maximumWealth(array));
    }

    public static int maximumWealth(int[][] accounts) {


        int richest=Integer.MIN_VALUE;
        int sum=0;
        for (int[] customer:accounts){
            for(int bal:customer){
                sum+=bal;
            }
            if(sum>richest){
                richest=sum;
                
            }
            sum=0;
        }
        return richest;
    }
}

