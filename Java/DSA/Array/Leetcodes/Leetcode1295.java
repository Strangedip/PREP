// find count of even digited numbers in array
public class Leetcode1295 {
    public static void main(String[] args) {
        int[] array={12,0,-1234,345,2,6,7896};
        System.out.println(findNumbers(array));
    }

    public static int findNumbers(int[] nums) {
        int count=0;
        for (int num:nums){
            //if digit is even
            if (countDigit(num)%2==0){
                count++;
            }
        }
        return count;
    }
    public static int countDigit(int num){
        
        //edge cases
        if (num==0){
            return 1;
        }
        if (num<0){
            num=num*-1;
        }

        //digit counter
        int digit = 0;
        while(num>0){
            num/=10;
            digit++;
        }
        return digit;
    }
}
