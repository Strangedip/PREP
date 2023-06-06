// highest candies one can have
// Input: candies = [2,3,5,1,3], extraCandies = 3 
// Output: [true,true,true,false,true] i.e 2+3=5,3+3=6 is highest hence true

import java.util.ArrayList;
import java.util.List;

public class Leetcode1431 {
    public static void main(String[] args) {
        int[] array= {4,2,1,1,2};
        int candy=1;
        System.out.println(kidsWithCandies(array,candy));
    }
    public static List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        List<Boolean> ans=new ArrayList<Boolean>();
        // List<Boolean> ans=new ArrayList<Boolean>(Arrays.asList(new Boolean[10]));
        int max=highest(candies);
        for(int i=0;i<candies.length;i++){
            if (candies[i]+extraCandies>=max){
                ans.add(i, true);
            }
            else{
                ans.add(i,false);
            }
        }
        return ans;
    }
    public static int highest(int[] candies){
        int max=candies[0];
        for(int i=1;i<candies.length;i++)
        {
            if (candies[i]>max){
                max=candies[i];
            }
        }
        return max;
    }
}
