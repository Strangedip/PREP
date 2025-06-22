//Bitonic array [ascending-> <-descending]
//find peak in bitonic array with exception arrays too (similar to 852 but with edge cases)

public class Leetcode162 {
    public static void main(String[] args) {
        int[] array={-1,-3,1,2,3,4,4,4,4,3,2,1,0,-2};
        System.out.println(peakElement(array));
    }
    public static int peakElement(int[] nums){
        int start=0;
        int end=nums.length-1;
        if(end==0){
            return 0;
        }
        int middle;
        while(start<=end){
            middle=(start+end)/2;
            if (middle==nums.length-1){          
                return middle;
            }
            if(nums[middle]>nums[middle+1]){
                end=middle-1;
            }
            else if (nums[middle]<=nums[middle+1]){
                start=middle+1;
            }
        }
        //eventually searching array will stop at peak element
        return start;
    }
}

// Uses binary search to find peak. since array is sorted ascending a<b<c..., peak will be current -> current > next.
