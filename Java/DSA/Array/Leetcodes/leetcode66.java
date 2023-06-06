// return array by adding 1 in array as one number 
// [9,8] -> [9,9] or [9,9] -> [1,0,0]

import java.util.Arrays;

public class leetcode66 {
    public static void main(String[] args) {
        int[] digits = {9,8,7,6,5,4,3,2,1,0 };
        System.out.println(Arrays.toString(digit(digits)));

    }

    public static int[] digit(int[] digits){

        // starting from last element of array
        for(int i =digits.length-1;i>=0;i--){
            //if element is less than 9 simply return +1 value
            if(digits[i]<9){
                digits[i]++;
                return digits;
            }
            //else make that element 0 and move to i-- in for loop
            digits[i]=0;
            
        }
        // if not returned yet means all elemnt are 9 in array which are 0 now
        // hence create new array with +1 size and add 1st element as 1 i.e [0,0] -> [1,0,0]
        int [] newDigits=new int[digits.length+1];
        newDigits[0]=1;
        return newDigits;

    }
}
