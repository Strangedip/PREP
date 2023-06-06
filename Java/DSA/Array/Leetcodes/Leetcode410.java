// return highest sum of k subarrays made from an array
// A subarray is a contiguous part of the array. 

public class Leetcode410 {
    public static void main(String[] args) {
        int[] arr = { 7, 2, 5, 10, 8 };
        // number of subarrays to make
        int k = 2;
        System.out.println(splitArray(arr, k));
    }

    public static int splitArray(int[] nums, int k) {
        int start = 0;
        int end = 0;
        // getting highest sum and lowest sum in array by size arr.lenght and 1
        // start=10, end=32
        for (int i = 0; i < nums.length; i++) {
            start = Math.max(start, nums[i]);
            end += nums[i];
        }

        while (start < end) {
            // creating average if start,end
            int middle = (start + end) / 2;
            //added counter which counts subarrays
            int count = 1;
            //sum to be reset once for every continoeous elemnt added and compared with old sum  
            int sum = 0;
            for (int num:nums) {
                //if sum is gets highesr value than middle if yes increase number of subarrays and reset sum to num
                if (sum + num > middle) {
                    sum = num;
                    count++;
                } else {
                    //else keep adding in sum
                    sum += num;
                }
            }
            //check if number of subarray is larger tha target subarray k
            //if yes increase the middle by start=middle+1
            if (count > k) {
                start = middle + 1;
            } else {
                //else put end = middle to deacrese middle value for highest subarray sum
                end = middle;
            }
        }
        //return the highest sum stored in middle as end 
        return end;
    }

}
